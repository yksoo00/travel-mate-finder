package com.multi.ouigo.domain.recruit.dto.res;

import com.multi.ouigo.domain.approval.dto.res.ApprovalResDto;
import com.multi.ouigo.domain.condition.dto.res.ConditionResDto;
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
public class RecruitResDto {

    private String recruitTitle;
    private String recruitContent;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<ApprovalResDto> approvals;
    private List<ConditionResDto> conditions;
    private String touristSpotAddress;
    private String memberName;
    private int memberAge;
    private String memberGender;

}
