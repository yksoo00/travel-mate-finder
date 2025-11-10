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
public class ApprovalResDto {

    private ApprovalStatus status;
}
