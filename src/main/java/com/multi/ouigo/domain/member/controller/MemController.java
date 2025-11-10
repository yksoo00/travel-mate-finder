package com.multi.ouigo.domain.member.controller;

import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.member.dto.res.MemResDto;
import com.multi.ouigo.domain.member.service.MemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/myPage")
public class MemController {

    private final MemService memService;

     // 마이페이지 - 전체 정보 조회 GET (/api/v1/mypage)

    @GetMapping
    public ResponseEntity<ResponseDto> getMemberProfile(@RequestParam(name = "memberNo") Long memberNo)
        {

        log.info("[MemController] 회원 정보 조회 - memberNo: {}", memberNo);

        return ResponseEntity.ok()
                .body(new ResponseDto(
                        HttpStatus.OK,
                        "회원 정보 조회 성공",
                        memService.getMemberProfile(memberNo)
                ));
        }



    // 마이페이지 - 기본 정보 수정 PUT (/api/v1/mypage)



    @PutMapping
    public ResponseEntity<ResponseDto> updateMemberProfile(
            @RequestParam(name = "memberNo") Long memberNo,
            @RequestBody MemResDto memResDto) {

        log.info("[MemController] 회원 정보 수정 - memberNo: {}", memberNo);

        return ResponseEntity.ok()
                .body(new ResponseDto(
                        HttpStatus.OK,
                        "회원 정보 수정 성공",
                        memService.updateMemberProfile(memberNo, memResDto)
                ));
    }

}
