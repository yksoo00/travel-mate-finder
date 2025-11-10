package com.multi.ouigo.domain.member.entity;

import com.multi.ouigo.common.entitiy.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;


@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Long no;  // 회원번호

    @Column(name = "id", nullable = false, unique = true)
    private String memberId;  // 로그인 아이디(중복불가)

    @Column(name = "pw", nullable = false)
    private String password;  // 비밀번호(암호화)

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Column(name = "email", nullable = false, unique = true)
    private String email;  // 이메일

    @Setter
    @Column(name = "role", nullable = false)
    private String role;  // 사용자/관리자 권한

    @Column(name = "nick_nm", nullable = false, unique = true, length = 6)
    private String nickName;  // 닉네임(중복불가, 최대 6글자)

    @Column(name = "prof_img", length = 500)
    private String profileImage;  // 프로필 이미지 URL

    @Column(name = "age")
    private int age;  // 나이

    @Column(name = "gndr", length = 1)
    private String gender;  // 성별

    @Column(name = "intr", length = 150)
    private String introduction; // 자기소개


    public void update(String nickName, String email, String profileImage, String introduction) {
        this.nickName = nickName;
        this.email = email;
        this.profileImage = profileImage;
        this.introduction = introduction;
    }
}
