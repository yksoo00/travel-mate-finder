package com.multi.ouigo.domain.trip.controller;

import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.trip.dto.req.TripReqDto;
import com.multi.ouigo.domain.trip.service.TripService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/myPage/trips")
public class TripController {

    private final TripService tripService;

    // 여행 일정 등록

    @PostMapping
    public ResponseEntity<ResponseDto> createTrip(HttpServletRequest request,
        @Valid @RequestBody TripReqDto tripReqDto) {

        log.info("[TripController] 여행 일정 등록 - memberNo: {}", request);

        return ResponseEntity.ok()
            .body(new ResponseDto(
                HttpStatus.CREATED,
                "여행 일정 등록 성공",
                tripService.createTrip(request, tripReqDto)
            ));
    }

    // 여행 일정 목록 조회

    @GetMapping
    public ResponseEntity<ResponseDto> getTripList(
        HttpServletRequest request) {

        return ResponseEntity.ok()
            .body(new ResponseDto(
                HttpStatus.OK,
                "여행 일정 목록 조회 성공",
                tripService.getTripList(request)
            ));
    }


    // 여행 일정 수정
    @PutMapping("/{tripId}")
    public ResponseEntity<ResponseDto> updateTrip(
        @PathVariable Long tripId,
        @Valid @RequestBody TripReqDto tripReqDto) {

        log.info("[TripController] 여행 일정 수정 - tripId: {}", tripId);

        return ResponseEntity.ok()
            .body(new ResponseDto(
                HttpStatus.OK,
                "여행 일정 수정 성공",
                tripService.updateTrip(tripId, tripReqDto)
            ));
    }


    // 여행 일정 삭제
    @DeleteMapping("/{tripId}")
    public ResponseEntity<ResponseDto> deleteTrip(
        @PathVariable Long tripId) {

        log.info("[TripController] 여행 일정 삭제 - tripId: {}", tripId);

        tripService.deleteTrip(tripId);

        return ResponseEntity.ok()
            .body(new ResponseDto(
                HttpStatus.OK,
                "여행 일정 삭제 성공",
                null
            ));
    }
}
