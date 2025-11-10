package com.multi.ouigo.domain.member.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemReqDto {


    @NotNull(message = "닉네임은 필수 입력 사항입니다.")
    @Size(max = 6, message = "닉네임은 최대 6자여야 합니다.")
    private String nickname;

    @NotNull(message = "이메일은 필수 입력 사항입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    private String profileImage;

    @Size(max = 150, message = "자기소개는 150자 이내로 작성해주세요.")
    private String introduction;

}
