package com.multi.ouigo.domain.review.mapper;

import com.multi.ouigo.domain.review.dto.req.ReviewReqDTO;
import com.multi.ouigo.domain.review.dto.res.ReviewResDTO;
import com.multi.ouigo.domain.review.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {


    @Mapping(source = "member.nickname", target = "nickNm") // Member 닉네임
    @Mapping(source = "member.profileImage", target = "profImg")    // Member 프로필 이미지
    @Mapping(source = "tourist.id", target = "touristId")   // 관광지 ID
    @Mapping(source = "cont", target = "content")   // 리뷰 내용
    @Mapping(source = "id", target = "id")        // 리뷰 ID

    ReviewResDTO toResDto(Review review);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "tourist", ignore = true)
    @Mapping(source = "content", target = "cont")
    Review toEntity(ReviewReqDTO dto);
}