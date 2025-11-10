package com.multi.ouigo.domain.recruit.mapper;

import com.multi.ouigo.domain.recruit.dto.req.CreateRecruitReqDto;
import com.multi.ouigo.domain.recruit.dto.res.RecruitListResDto;
import com.multi.ouigo.domain.recruit.entity.Recruit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecruitMapper {

    @Mapping(target = "title", source = "recruitTitle")
    @Mapping(target = "content", source = "recruitContent")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "touristSpotId", source = "touristSpotId")
    @Mapping(target = "category", source = "recruitCategory")
    @Mapping(target = "approvals", ignore = true)
    @Mapping(target = "conditions", ignore = true)
    Recruit toEntity(CreateRecruitReqDto createRecruitReqDto);

    @Mapping(target = "memberName", expression = "java(recruit.getMember().getNickName())")
    @Mapping(target = "memberAge", expression = "java(recruit.getMember().getAge())")
    @Mapping(target = "memberGender", expression = "java(recruit.getMember().getGender())")
    @Mapping(source = "id", target = "recruitId")
    @Mapping(source = "title", target = "recruitTitle")
    @Mapping(source = "content", target = "recruitContent")
    RecruitListResDto toDto(Recruit recruit);
}
