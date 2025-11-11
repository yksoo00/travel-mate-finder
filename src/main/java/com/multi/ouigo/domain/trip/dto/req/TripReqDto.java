package com.multi.ouigo.domain.trip.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripReqDto {

    @NotBlank(message = "여행 지역은 필수 입력 사항입니다.")
    private String destination;  // 여행 지역

    @NotBlank(message = "여행 관광지는 필수 입력 사항입니다.")
    private String title;  // 여행 관광지

    @NotNull(message = "시작일은 필수 입력 사항입니다.")
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수 입력 사항입니다.")
    private LocalDate endDate;

    private int budget;

    @Size(max = 100, message = "여행 메모는 최대 100자입니다.")
    private String memo;

}
