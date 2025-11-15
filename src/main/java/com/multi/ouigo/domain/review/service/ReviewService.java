package com.multi.ouigo.domain.review.service;

import com.multi.ouigo.common.exception.custom.NotFindException;
import com.multi.ouigo.common.jwt.provider.TokenProvider;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.member.repository.MemberRepository;
import com.multi.ouigo.domain.review.dto.req.ReviewReqDTO;
import com.multi.ouigo.domain.review.dto.res.ReviewResDTO;
import com.multi.ouigo.domain.review.entity.Review;
import com.multi.ouigo.domain.review.mapper.ReviewMapper;
import com.multi.ouigo.domain.review.repository.ReviewRepository;
import com.multi.ouigo.domain.tourist.entity.TouristSpot;
import com.multi.ouigo.domain.tourist.repository.TouristSpotRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    public final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final TouristSpotRepository touristRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public Page<ReviewResDTO> selectReviewTotalByTourist(Long touristId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByTouristIdWithPaging(touristId, pageable);

        return reviews.map(review -> {
            return reviewMapper.toResDto(review);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public Long registerReview(Long touristId, ReviewReqDTO reviewReqDTO,
        HttpServletRequest request) {

        TouristSpot tourist = touristRepository.findById(touristId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관광지입니다."));

        String memberId = tokenProvider.extractMemberId(request);

        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotFindException("없는 멤버입니다"));

        Review review = reviewMapper.toEntity(reviewReqDTO);

        review.setMember(member);  // 양방향 연관관계
        tourist.addReview(review);

        reviewRepository.save(review);

        return review.getTourist().getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Long updateReview(Long reviewId, ReviewReqDTO reviewReqDTO, HttpServletRequest request) {

        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));

        String currentMemberId = tokenProvider.extractMemberId(request);

        if (!review.getMember().getMemberId().equals(currentMemberId)) {
            throw new IllegalArgumentException("리뷰 수정 권한이 없습니다.");
        }

        review.update(reviewReqDTO.getContent());

        return review.getTourist().getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Long deleteReview(Long reviewId, HttpServletRequest request) {

        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        String currentMemberId = tokenProvider.extractMemberId(request);

        if (!review.getMember().getMemberId().equals(currentMemberId)) {
            throw new IllegalArgumentException("리뷰 삭제 권한이 없습니다.");
        }

        review.setDeleted(true);

        review.getTourist().getReviews().remove(review);
        return review.getTourist().getId();
    }


}
