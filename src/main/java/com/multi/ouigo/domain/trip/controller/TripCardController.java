package com.multi.ouigo.domain.trip.controller;

import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.trip.service.TripCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/myPage")
public class TripCardController {

    private final TripCardService tripCardService;

    // 여행 카드 조회 GET /api/v1/myPage/card?memberNo=1

    @GetMapping("/card")
    public ResponseEntity<ResponseDto> getTripCard(
            @RequestParam(name = "memberNo") Long memberNo) {

        log.info("[TripCardController] 여행 카드 조회 - memberNo: {}", memberNo);

        try {
            return ResponseEntity.ok()
                    .body(new ResponseDto(
                            HttpStatus.OK,
                            "여행 카드 조회에 성공했습니다.",
                            tripCardService.getTripCard(memberNo)
                    ));

        } catch (IllegalArgumentException e) {

            // 404: 여행 일정을 찾을 수 없습니다.
            log.error("여행 카드 조회 실패 - memberNo: {}, error: {}", memberNo, e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDto(
                            HttpStatus.NOT_FOUND,
                            e.getMessage(),
                            null
                    ));

        } catch (Exception e) {

            // 500: 서버 오류
            log.error("여행 카드 조회 중 서버 오류 - memberNo: {}, error: {}", memberNo, e.getMessage());

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

