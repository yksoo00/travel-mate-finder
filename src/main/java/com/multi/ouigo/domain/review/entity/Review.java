package com.multi.ouigo.domain.review.entity;

import com.multi.ouigo.common.entitiy.BaseEntity;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.tourist.entity.TouristSpot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "del_yn = 0")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 리뷰 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;  // 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourist_id")
    private TouristSpot tourist;  // 관광지

    @Column(nullable = false, length = 1000)
    private String cont;  // 리뷰 내용

    public void setTourist(TouristSpot tourist) {       // 연관 관계
        this.tourist = tourist;
        if (tourist != null && !tourist.getReviews().contains(this)) {
            tourist.getReviews().add(this);
        }
    }

    public void setMember(Member member) {
        this.member = member;
    }


    public void update(String cont) {
        this.cont = cont;
    }

}

