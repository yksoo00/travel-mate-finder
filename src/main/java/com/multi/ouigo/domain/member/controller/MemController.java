package com.multi.ouigo.domain.member.controller;

import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.member.dto.res.MemResDto;
import com.multi.ouigo.domain.member.service.MemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/myPage")
public class MemController {

    private final MemService memService;

    // 마이페이지 - 전체 정보 조회 GET (/api/v1/mypage)

    @GetMapping
    public ResponseEntity<ResponseDto> getMemberProfile(HttpServletRequest request) {

        return ResponseEntity.ok()
            .body(new ResponseDto(
                HttpStatus.OK,
                "회원 정보 조회 성공",
                memService.getMemberProfile(request)
            ));
    }

    // 마이페이지 - 기본 정보 수정 PUT (/api/v1/mypage)

    @PutMapping
    public ResponseEntity<ResponseDto> updateMemberProfile(
        HttpServletRequest request,
        @RequestBody MemResDto memResDto) {

        return ResponseEntity.ok()
            .body(new ResponseDto(
                HttpStatus.OK,
                "회원 정보 수정 성공",
                memService.updateMemberProfile(request, memResDto)
            ));
    }

    // 프로필 이미지 업로드
    @PostMapping("/profile/image")
    public ResponseEntity<ResponseDto> uploadProfileImage(
        HttpServletRequest request,
        @RequestParam("file") MultipartFile file) {  // ✅ 파일 받기!

        String imageUrl = memService.uploadProfileImage(request, file);

        return ResponseEntity.ok()
            .body(new ResponseDto(
                HttpStatus.OK,
                "프로필 이미지 업로드 성공",
                imageUrl
            ));

    }
}
