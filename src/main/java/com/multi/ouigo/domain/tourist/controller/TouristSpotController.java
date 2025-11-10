package com.multi.ouigo.domain.tourist.controller;


import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.tourist.dto.req.TouristSpotReqDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotAllResDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotResDto;
import com.multi.ouigo.domain.tourist.service.TouristSpotService;
import jakarta.validation.Valid;
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

    @PostMapping("/tourist-spots")
    public ResponseEntity<ResponseDto> regist(@ModelAttribute TouristSpotReqDto touristSpotReqDto){
        System.out.println("touristSpotReqDto : "+touristSpotReqDto.getTitle());
        Long id = touristSpotService.regist(touristSpotReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto(HttpStatus.CREATED,"관광지 등록 성공",id));


    }
    @PutMapping("/tourist-spots/{id}")
    public ResponseEntity<ResponseDto> updateById(@PathVariable Long id, @ModelAttribute @Valid TouristSpotReqDto touristSpotReqDto){
        touristSpotService.updateById(id,touristSpotReqDto);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK,"관광지 수정 성공",id));
    }
}
