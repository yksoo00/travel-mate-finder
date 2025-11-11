package com.multi.ouigo.domain.review.controller;

import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.review.dto.req.ReviewReqDTO;
import com.multi.ouigo.domain.review.dto.res.ReviewResDTO;
import com.multi.ouigo.domain.review.service.ReviewService;
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
@RequestMapping("api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;


    @GetMapping("/{id}")    // 리뷰 pageable 조회
    public ResponseEntity<ResponseDto> getReviewsByTourist(@PathVariable("id") Long touristId,
                                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                                           @RequestParam(name = "size", defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ReviewResDTO> reviews = reviewService.selectReviewTotalByTourist(touristId, pageable);

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "리뷰 조회 성공", reviews));

    }

    @PostMapping("/{id}")   // 리뷰 등록
    public ResponseEntity<ResponseDto> registerReviewByNo(@PathVariable("id") Long touristId,
                                                          @RequestBody ReviewReqDTO reviewReqDTO,
                                                          @RequestParam(name = "memberNo") Long memberNo) {
        Long findTouristId = reviewService.registerReview(touristId, reviewReqDTO, memberNo);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.CREATED, "리뷰 등록 성공", findTouristId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateReview(@PathVariable("id") Long reviewId,
                                                    @RequestBody ReviewReqDTO reviewReqDTO) {
        Long touristId = reviewService.updateReview(reviewId, reviewReqDTO);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "리뷰 수정 성공", touristId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteReview(@PathVariable("id") Long reviewId){


        Long touristId = reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "리뷰가 삭제되었습니다.", touristId));
    }


}
