package com.multi.ouigo.domain.recruit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.multi.ouigo.common.entitiy.BaseEntity;
import com.multi.ouigo.domain.approval.entity.Approval;
import com.multi.ouigo.domain.condition.entity.Condition;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.recruit.dto.req.UpdateRecruitReqDto;
import com.multi.ouigo.domain.tourist.entity.TouristSpot;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "recruits")
@Where(clause = "del_yn = 0")
public class Recruit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourist_spot_id")
    private TouristSpot touristSpot;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;

    @Column(name = "ttl", nullable = false)
    private String title;

    @Column(name = "cntn", nullable = false)
    private String content;

    @Column(name = "ctgry", nullable = false)
    private String category;

    @Column(name = "strt_dt", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "end_dt", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Condition> conditions = new ArrayList<>();

    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Approval> approvals = new ArrayList<>();

    public void addApproval(Approval approval) {
        this.approvals.add(approval);
        approval.setRecruit(this);
    }

    public void addCondition(Condition condition) {
        if (this.conditions == null) {
            this.conditions = new ArrayList<>();
        }
        this.conditions.add(condition);
        condition.setRecruit(this);
    }

    @Builder
    public Recruit(String title, String content, String category, Long touristSpotId,
        LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approvals = new ArrayList<>();
        this.conditions = new ArrayList<>();
    }


    public void updateRecruit(UpdateRecruitReqDto updateRecruitReqDto) {
        this.title = updateRecruitReqDto.getRecruitTitle();
        this.content = updateRecruitReqDto.getRecruitContent();
        this.category = updateRecruitReqDto.getRecruitCategory();
        this.startDate = updateRecruitReqDto.getStartDate();
        this.endDate = updateRecruitReqDto.getEndDate();
    }
}
