package com.multi.ouigo.domain.trip.entity;

import com.multi.ouigo.common.entitiy.BaseEntity;
import com.multi.ouigo.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "TB_OUI_TCD")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;  // 기본키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member member;  // 카드 소유자 (FK)

    @Column(name = "dstc_cnt", nullable = false)
    private int destinationCount;  // 방문한 지역수

    @Column(name = "ttl_cnt", nullable = false)
    private int titleCount;  // 방문한 관광지수

    @Column(name = "day_cnt", nullable = false)
    private int dayCount;  // 총 여행 일수


    // 통계 업데이트

    public void updateStatistics(int destinationCount, int titleCount, int dayCount) {
        this.destinationCount = destinationCount;
        this.titleCount = titleCount;
        this.dayCount = dayCount;
    }
}
