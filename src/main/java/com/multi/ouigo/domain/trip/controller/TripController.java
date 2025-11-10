package com.multi.ouigo.domain.trip.controller;

import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.trip.dto.req.TripReqDto;
import com.multi.ouigo.domain.trip.entity.Trip;
import com.multi.ouigo.domain.trip.repository.TripRepository;
import com.multi.ouigo.domain.trip.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/myPage/trips")
public class TripController {

    private final TripService tripService;

    // 여행 일정 등록

    @PostMapping
    public ResponseEntity<ResponseDto> createTrip( @RequestParam(name = "memberNo") Long memberNo,
                                                   @Valid @RequestBody TripReqDto tripReqDto) {

        log.info("[TripController] 여행 일정 등록 - memberNo: {}", memberNo);

        return ResponseEntity.ok()
                .body(new ResponseDto(
                        HttpStatus.CREATED,
                        "여행 일정 등록 성공",
                        tripService.createTrip(memberNo, tripReqDto)
                ));
    }


    // 여행 일정 목록 조회

    @GetMapping
    public ResponseEntity<ResponseDto> getTripList(
            @RequestParam(name = "memberNo") Long memberNo) {

        log.info("[TripController] 여행 일정 목록 조회 - memberNo: {}", memberNo);

        return ResponseEntity.ok()
                .body(new ResponseDto(
                        HttpStatus.OK,
                        "여행 일정 목록 조회 성공",
                        tripService.getTripList(memberNo)
                ));
    }

}
