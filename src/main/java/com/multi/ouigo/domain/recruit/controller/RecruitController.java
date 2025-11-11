package com.multi.ouigo.domain.recruit.controller;

import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.common.response.ResponseNoDateDto;
import com.multi.ouigo.domain.recruit.dto.req.CreateRecruitReqDto;
import com.multi.ouigo.domain.recruit.dto.req.UpdateRecruitReqDto;
import com.multi.ouigo.domain.recruit.entity.Recruit;
import com.multi.ouigo.domain.recruit.service.RecruitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/recruit")
public class RecruitController {

    private final RecruitService recruitService;

    @PostMapping
    public ResponseEntity<ResponseDto> createRecruit(
        @RequestBody @Valid CreateRecruitReqDto createRecruitReqDto,
        HttpServletRequest request) {

        Recruit recruit = recruitService.createRecruit(request, createRecruitReqDto);

        return ResponseEntity.ok()
            .body(new ResponseDto(HttpStatus.CREATED, "모집 글 생성", recruit.getId()));
    }

    @GetMapping
    public ResponseEntity<ResponseDto> findAllRecruit(HttpServletRequest request,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "3") int size,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok()
            .body(new ResponseDto(HttpStatus.OK, "모집 전체 조회 성공",
                recruitService.findAllRecruit(request, pageable,
                    category,
                    startDate, endDate)));


    }

    @GetMapping("/{recruitId}")
    public ResponseEntity<ResponseDto> findRecruit(HttpServletRequest request,
        @PathVariable(name = "recruitId") Long recruitId) {
        return ResponseEntity.ok()
            .body(new ResponseDto(HttpStatus.OK, "모집 상세 보기 성공",
                recruitService.findRecruit(request, recruitId)));
    }

    @PutMapping("/{recruitId}")
    public ResponseEntity<ResponseDto> updateRecruit(HttpServletRequest request,
        @PathVariable(name = "recruitId") Long recruitId,
        @RequestBody UpdateRecruitReqDto dto) {
        return ResponseEntity.ok()
            .body(new ResponseDto(HttpStatus.OK, "모집 글 수정 성공",
                recruitService.updateRecruit(request, recruitId, dto)));
    }

    @DeleteMapping("/{recruitId}")
    public ResponseEntity<ResponseDto> deleteRecruit(HttpServletRequest request,
        @PathVariable(name = "recruitId") Long recruitId) {
        recruitService.deleteRecruit(request, recruitId);
        return ResponseEntity.ok()
            .body(new ResponseDto(HttpStatus.NO_CONTENT, "모집 글 삭제 성공",
                null));
    }

    @PostMapping("/participation/{recruitId}")
    public ResponseEntity<ResponseNoDateDto> participateRecruit(HttpServletRequest request,
        @PathVariable(name = "recruitId") Long recruitId) {
        recruitService.participateRecruit(request, recruitId);

        return ResponseEntity.ok()
            .body(new ResponseNoDateDto(HttpStatus.NO_CONTENT, "참여 신청 되었습니다"));
    }

}
