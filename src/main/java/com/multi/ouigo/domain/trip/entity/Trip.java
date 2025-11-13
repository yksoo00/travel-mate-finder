package com.multi.ouigo.domain.trip.entity;

import com.multi.ouigo.common.entitiy.BaseEntity;
import com.multi.ouigo.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;


@Entity
@Table(name = "trips")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "del_yn = 0")
public class Trip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;  // 여행 일정 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member;  // 회원(FK)

    @Column(name = "dstc", nullable = false)
    private String destination;  // 여행 지역

    @Column(name = "ttl", nullable = false)
    private String title;  // 여행 관광지

    @Column(name = "st_dt", nullable = false)
    private LocalDate startDate;  // 여행 시작일

    @Column(name = "ed_dt", nullable = false)
    private LocalDate endDate;  // 여행 종료일

    @Column(name = "dur", nullable = false)
    private int duration;  // 여행 일수

    @Column(name = "bdgt")
    private int budget;  // 여행 예산

    @Column(name = "memo", length = 100)
    private String memo;  // 여행 메모 (최대 100자)


    // 여행 일수 자동 계산
    @PrePersist
    @PreUpdate
    public void calculateDuration() {

        if (startDate != null && endDate != null) {

            this.duration = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }

    }

    // D-day 계산

    public long getDday() {

        if (startDate == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        return ChronoUnit.DAYS.between(startDate, today);
    }


    // 여행 일정 수정
    public void update(String destination, String title, LocalDate startDate,
        LocalDate endDate, int budget, String memo) {
        this.destination = destination;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.memo = memo;
        calculateDuration();
    }

}
