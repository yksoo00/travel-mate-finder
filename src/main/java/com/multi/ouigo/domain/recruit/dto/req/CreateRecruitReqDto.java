package com.multi.ouigo.domain.recruit.dto.req;

import com.multi.ouigo.domain.condition.constant.AgeCode;
import com.multi.ouigo.domain.condition.constant.GenderCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRecruitReqDto {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 최대 100글자까지 가능합니다.")
    private String recruitTitle;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 1000, message = "내용은 최대 1000글자까지 가능합니다.")
    private String recruitContent;

    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수입니다.")
    private LocalDate endDate;

    private String recruitCategory;
    private List<GenderCode> genderCodes;

    private List<AgeCode> ageCodes;

    private int touristSpotId;


}
