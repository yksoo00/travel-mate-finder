package com.multi.ouigo.domain.review.entity;

import com.multi.ouigo.common.entitiy.BaseEntity;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.tourist.entity.Tourist;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 리뷰 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourist_id")
    private Tourist tourist;  // 관광지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;  // 작성자

    @Column(nullable = false, length = 1000)
    private String cont;  // 리뷰 내용

    public void setTourist(Tourist tourist) {
        this.tourist = tourist;
    }

    public void setMember(Member member) {
        this.member = member;
    }


}

