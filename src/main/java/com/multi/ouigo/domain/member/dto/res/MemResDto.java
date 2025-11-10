package com.multi.ouigo.domain.member.dto.res;


import com.multi.ouigo.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 개인정보 수정 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemResDto {

    private Long no;
    private String memberId;
    private String email;
    private String nickname;
    private String profileImage;
    private String introduction;


    public static MemProfResDto from(Member member) {
        return MemProfResDto.builder()
                .no(member.getNo())
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .introduction(member.getIntroduction())
                .build();
    }


}
