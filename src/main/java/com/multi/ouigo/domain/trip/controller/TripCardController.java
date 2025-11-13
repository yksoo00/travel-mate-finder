package com.multi.ouigo.domain.trip.controller;

import com.multi.ouigo.common.exception.custom.NotFindException;
import com.multi.ouigo.common.jwt.provider.TokenProvider;
import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.member.repository.MemberRepository;
import com.multi.ouigo.domain.trip.service.TripCardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/myPage")
public class TripCardController {

    private final TripCardService tripCardService;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    // 여행 카드 조회 GET /api/v1/myPage/card?memberNo=1

    @GetMapping("/card")
    public ResponseEntity<ResponseDto> getTripCard(
        HttpServletRequest request) {

        String memberId = tokenProvider.extractMemberId(request);
        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotFindException("없는 멤버입니다"));

        try {
            return ResponseEntity.ok()
                .body(new ResponseDto(
                    HttpStatus.OK,
                    "여행 카드 조회에 성공했습니다.",
                    tripCardService.getTripCard(member.getNo())
                ));

        } catch (IllegalArgumentException e) {

            // 404: 여행 일정을 찾을 수 없습니다.

            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseDto(
                    HttpStatus.NOT_FOUND,
                    e.getMessage(),
                    null
                ));

        } catch (Exception e) {

            // 500: 서버 오류

            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDto(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "여행 카드 조회에 실패했습니다.",
                    null
                ));
        }
    }
}

