package com.multi.ouigo.domain.trip.dto.res;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 여행 일정 등록, 수정 DTO

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripResDto {

    private Long id;  // 수정, 삭제 시 필요
    private String destination;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private int duration;
    private int budget;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
