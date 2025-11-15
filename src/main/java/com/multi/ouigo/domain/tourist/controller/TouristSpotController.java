package com.multi.ouigo.domain.tourist.controller;


import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.tourist.dto.req.TouristSpotReqDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotAllResDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotResDto;
import com.multi.ouigo.domain.tourist.service.TouristSpotService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TouristSpotController {

    private final TouristSpotService touristSpotService;

    @GetMapping("/tourist-spots/markers")
    public ResponseEntity<ResponseDto> getTouristSpots() {
        List<TouristSpotResDto> touristSpots = touristSpotService.getTouristSpots();
        return ResponseEntity.ok()
            .body(new ResponseDto(HttpStatus.OK, "마커에 표시할 모든 관광지 조회 완료", touristSpots));
    }

    @GetMapping("/tourist-spots")
    public ResponseEntity<ResponseDto> getTouristSpots(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(name = "keyword", required = false) String keyword) {
        String message = "";
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<TouristSpotResDto> touristSpots = touristSpotService.getTouristSpots(keyword,
            pageable);

        if (keyword == null) {
            message = "관광지 전체 목록 조회 완료";
        } else {
            message = "키워드 기반 페이지별 관광지 목록 조회 성공";
        }
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, message, touristSpots));
    }


    @GetMapping("/tourist-spots/{id}")
    public ResponseEntity<ResponseDto> getTouristSpotById(@PathVariable Long id) {

        TouristSpotAllResDto touristSpot = touristSpotService.getTouristSpotById(id);
        return ResponseEntity.ok()
            .body(new ResponseDto(HttpStatus.OK, "관광지 상세 조회 성공", touristSpot));
    }

    @PostMapping("/tourist-spots")
    public ResponseEntity<ResponseDto> save(
        @ModelAttribute @Valid TouristSpotReqDto touristSpotReqDto) {
        System.out.println("touristSpotReqDto : " + touristSpotReqDto.getTitle());
        Long id = touristSpotService.save(touristSpotReqDto);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ResponseDto(HttpStatus.CREATED, "관광지 등록 성공", id));


    }

    @PutMapping("/tourist-spots/{id}")
    public ResponseEntity<ResponseDto> updateById(@PathVariable Long id,
        @ModelAttribute @Valid TouristSpotReqDto touristSpotReqDto) {
        touristSpotService.updateById(id, touristSpotReqDto);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "관광지 수정 성공", id));
    }

    @DeleteMapping("/tourist-spots/{id}")
    public ResponseEntity<ResponseDto> deleteById(@PathVariable Long id) {
        touristSpotService.deleteById(id);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "관광지 삭제 성공", id));
    }


    @GetMapping(value = "/tourist-spots/images", produces = MediaType.APPLICATION_JSON_VALUE)
    // 1. JSON으로 변경
    public ResponseEntity<String> getTouristImages(@RequestParam String keyword) {
        String jsonResult = touristSpotService.getImages(keyword);
        System.out.println("jsonResult : " + jsonResult);

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(jsonResult);
    }
}
