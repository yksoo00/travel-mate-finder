package com.multi.ouigo.domain.member.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSignInDto {

    private String memberId;
    private String memberPassword;

}
