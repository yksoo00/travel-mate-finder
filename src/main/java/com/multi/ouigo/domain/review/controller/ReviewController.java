package com.multi.ouigo.domain.review.controller;

import com.multi.ouigo.common.response.ResponseDto;
import com.multi.ouigo.domain.review.dto.req.ReviewReqDTO;
import com.multi.ouigo.domain.review.dto.res.ReviewResDTO;
import com.multi.ouigo.domain.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
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
                                                          HttpServletRequest request) {
        Long findTouristId = reviewService.registerReview(touristId, reviewReqDTO, request);
        return ResponseEntity.ok()
                .body(new ResponseDto(HttpStatus.CREATED, "리뷰 등록 성공", findTouristId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateReview(@PathVariable("id") Long reviewId,
                                                    @RequestBody ReviewReqDTO reviewReqDTO,
                                                    HttpServletRequest request) {
        Long touristId = reviewService.updateReview(reviewId, reviewReqDTO, request);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "리뷰 수정 성공", touristId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteReview(@PathVariable("id") Long reviewId,
                                                    HttpServletRequest request) {

        Long touristId = reviewService.deleteReview(reviewId, request);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "리뷰가 삭제되었습니다.", touristId));
    }


}
