package com.multi.ouigo.domain.recruit.controller;

import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.recruit.dto.req.CreateRecruitReqDto;
import com.multi.ouigo.domain.recruit.entity.Recruit;
import com.multi.ouigo.domain.recruit.service.RecruitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
