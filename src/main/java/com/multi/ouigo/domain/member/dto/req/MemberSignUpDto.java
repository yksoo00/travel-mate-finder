package com.multi.ouigo.domain.member.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSignUpDto {

    private String memberId;
    private String memberPassword;
    private String memberName;
    private String memberEmail;
    private String memberAge;
    private String memberGender;
    private String memberIntro;

}