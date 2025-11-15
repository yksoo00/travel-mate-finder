package com.multi.ouigo.domain.approval.entity;

import com.multi.ouigo.common.entitiy.BaseEntity;
import com.multi.ouigo.domain.approval.constant.ApprovalStatus;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.recruit.entity.Recruit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "approvals")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Where(clause = "del_yn = 0")
public class Approval extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "recruit_id")
    @ManyToOne
    private Recruit recruit;

    @JoinColumn(name = "member_no")
    @ManyToOne
    private Member member;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "apl_sts")
    private ApprovalStatus status;


    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = ApprovalStatus.PENDING;
        }
    }

    public void setRecruit(Recruit recruit) {
        this.recruit = recruit;
        if (recruit != null && !recruit.getApprovals().contains(this)) {
            recruit.getApprovals().add(this);
        }
    }
}
