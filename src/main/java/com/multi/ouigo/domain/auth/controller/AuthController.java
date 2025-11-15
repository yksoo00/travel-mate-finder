package com.multi.ouigo.domain.auth.controller;

import com.multi.ouigo.common.jwt.dto.TokenDto;
import com.multi.ouigo.common.jwt.service.TokenService;
import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.auth.service.AuthService;
import com.multi.ouigo.domain.member.dto.req.MemberSignInDto;
import com.multi.ouigo.domain.member.dto.req.MemberSignUpDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signup(@RequestBody MemberSignUpDto memberSignUpDto) {
        authService.signUp(memberSignUpDto);
        return ResponseEntity.ok(
            new ResponseDto(HttpStatus.CREATED, "회원가입 성공", null));

    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody MemberSignInDto memberSignInDto) {

        TokenDto token = authService.login(memberSignInDto);
        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "로그인 성공", token));

    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto> refresh(
        @RequestHeader("Authorization") String refreshToken) {
        TokenDto token = tokenService.createToken(refreshToken);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "로그인 성공", token));
    }

    @GetMapping("/logout")
    public ResponseEntity<ResponseDto> logout(@RequestHeader("Authorization") String accessToken) {
        tokenService.registBlackList(accessToken);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "로그아웃 성공", null));
    }


    @GetMapping("/me")
    public ResponseEntity<ResponseDto> getCurrentUser(HttpServletRequest request) {
        try {
            String memberId = tokenService.getMemberIdFromAccessToken(request);

            return ResponseEntity.ok(
                    new ResponseDto(HttpStatus.OK, "로그인 사용자 조회 성공", memberId)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDto(HttpStatus.UNAUTHORIZED, "로그인되지 않은 사용자", null));
        }
    }



}
