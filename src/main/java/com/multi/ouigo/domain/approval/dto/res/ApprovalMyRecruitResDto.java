package com.multi.ouigo.domain.approval.dto.res;

import com.multi.ouigo.domain.approval.constant.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovalMyRecruitResDto {

    private Long approvalId;
    private Long recruitId;
    private String memberNickName;
    private ApprovalStatus status;
    private String touristSpot;
    private Long memberNo;

}
