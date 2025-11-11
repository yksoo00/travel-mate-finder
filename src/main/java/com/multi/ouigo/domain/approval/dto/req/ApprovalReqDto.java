package com.multi.ouigo.domain.approval.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovalReqDto {

    private Long memberNo;
    private Long recruitId;

}
