package com.multi.ouigo.domain.member.dto.res;

import com.multi.ouigo.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 개인정보 전체 조회 DTO
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MemProfResDto {

    private Long no;
    private String memberId;
    private String email;
    private String nickName;
    private String profileImage;
    private Integer age;
    private String gender;
    private String introduction;


    public static MemProfResDto from(Member member) {
        return MemProfResDto.builder()
                .no(member.getNo())
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickName(member.getNickName())
                .profileImage(member.getProfileImage())
                .age(member.getAge())
                .gender(member.getGender())
                .introduction(member.getIntroduction())
                .build();
    }
}
