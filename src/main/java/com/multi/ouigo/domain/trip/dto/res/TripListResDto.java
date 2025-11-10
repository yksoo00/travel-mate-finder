package com.multi.ouigo.domain.trip.dto.res;

import lombok.*;

import java.time.LocalDate;

// 여행 일정 조회용 응답 DTO

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripListResDto {

    private Long id;
    private String destination;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer duration;
    private Integer budget;
    private Long dDay;
}
