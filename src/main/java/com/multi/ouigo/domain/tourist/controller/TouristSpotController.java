package com.multi.ouigo.domain.tourist.controller;


import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotAllResDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotResDto;
import com.multi.ouigo.domain.tourist.service.TouristSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TouristSpotController {

    private final TouristSpotService touristSpotService;

    @GetMapping("/tourist-spots")
    public ResponseEntity<ResponseDto> getTouristSpots(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<TouristSpotResDto> touristSpots = touristSpotService.getTouristSpots(pageable);

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK,"페이지별 관광지 목록 조회 성공",touristSpots));

    }


    @GetMapping("/tourist-spots/{id}")
    public ResponseEntity<ResponseDto> getTouristSpotById(@PathVariable Long id){

        TouristSpotAllResDto touristSpot= touristSpotService.getTouristSpotById(id);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK,"관광지 상세 조회 성공",touristSpot));
    }

}
