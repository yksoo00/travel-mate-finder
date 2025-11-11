package com.multi.ouigo.domain.approval.controller;

import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.common.response.ResponseNoDateDto;
import com.multi.ouigo.domain.approval.dto.req.ApprovalReqDto;
import com.multi.ouigo.domain.approval.service.ApprovalService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/approval")
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping("/approved")
    public ResponseEntity<ResponseNoDateDto> approvedRecruit(
        @RequestBody ApprovalReqDto approvalReqDto, HttpServletRequest request) {

        approvalService.approveRecruit(request, approvalReqDto);

        return ResponseEntity.ok().body(new ResponseNoDateDto(HttpStatus.NO_CONTENT, "승인 되었습니다"));
    }

    @PostMapping("/rejected")
    public ResponseEntity<ResponseNoDateDto> rejectedRecruit(
        @RequestBody ApprovalReqDto approvalReqDto, HttpServletRequest request) {
        approvalService.rejectedRecruit(request, approvalReqDto);
        return ResponseEntity.ok().body(new ResponseNoDateDto(HttpStatus.NO_CONTENT, "거절 되었습니다"));
    }

    @GetMapping("/MyRecruit")
    public ResponseEntity<ResponseDto> findAllMyRecruitApproval(HttpServletRequest request,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "내 모집글에 신청한 내역 조회 성공!",
            approvalService.findAllMyRecruitApproval(request, pageable)));
    }

    @GetMapping("/My")
    public ResponseEntity<ResponseDto> findAllMyApproval(HttpServletRequest request,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "내 모집글에 신청한 내역 조회 성공!",
            approvalService.findAllMyApproval(request, pageable)));
    }


}
