package com.multi.ouigo.domain.recruit.dto.res;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitListResDto {

    private Long recruitId;
    private String recruitTitle;
    private String recruitContent;
    private LocalDate startDate;
    private LocalDate endDate;
    private String memberName;
    private int memberAge;
    private String memberGender;


}
