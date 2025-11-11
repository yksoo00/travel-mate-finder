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
public class ApprovalMyResDto {

    private String memberNickName;
    private ApprovalStatus status;
    private int travelCountry;

}
