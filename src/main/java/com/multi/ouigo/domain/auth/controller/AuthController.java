package com.multi.ouigo.domain.auth.controller;

import com.multi.ouigo.common.jwt.dto.TokenDto;
import com.multi.ouigo.common.jwt.service.TokenService;
import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.auth.service.AuthService;
import com.multi.ouigo.domain.member.dto.req.MemberSignInDto;
import com.multi.ouigo.domain.member.dto.req.MemberSignUpDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
