package com.multi.ouigo.domain.recruit.mapper;

import com.multi.ouigo.domain.approval.dto.res.ApprovalResDto;
import com.multi.ouigo.domain.approval.entity.Approval;
import com.multi.ouigo.domain.condition.dto.res.ConditionResDto;
import com.multi.ouigo.domain.condition.entity.Condition;
import com.multi.ouigo.domain.recruit.dto.req.CreateRecruitReqDto;
import com.multi.ouigo.domain.recruit.dto.res.RecruitListResDto;
import com.multi.ouigo.domain.recruit.dto.res.RecruitResDto;
import com.multi.ouigo.domain.recruit.entity.Recruit;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RecruitMapper {

    @Mapping(target = "title", source = "recruitTitle")
    @Mapping(target = "content", source = "recruitContent")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
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

    @Mapping(target = "recruitTitle", source = "title")
    @Mapping(target = "recruitContent", source = "content")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "approvals", source = "approvals", qualifiedByName = "toApprovalDtoList")
    @Mapping(target = "conditions", source = "conditions", qualifiedByName = "toConditionDtoList")
    @Mapping(target = "touristSpotAddress", expression = "java(recruit.getTouristSpot().getAddress())")
    @Mapping(target = "touristSpotTitle", expression = "java(recruit.getTouristSpot().getTitle())")
    @Mapping(target = "memberAge", expression = "java(recruit.getMember().getAge())")
    @Mapping(target = "memberGender", expression = "java(recruit.getMember().getGender())")
    @Mapping(target = "memberName", expression = "java(recruit.getMember().getNickName())")
    @Mapping(target = "memberId", expression = "java(recruit.getMember().getMemberId())")
    RecruitResDto toResDto(Recruit recruit);

    @Named("toApprovalDtoList")
    default List<ApprovalResDto> toApprovalDtoList(List<Approval> approvals) {
        return approvals.stream()
            .map(a -> ApprovalResDto.builder()
                .memberName(a.getMember().getNickName())
                .memberAge(a.getMember().getAge())
                .memberGender(a.getMember().getGender())
                .status(a.getStatus())
                .build())
            .collect(Collectors.toList());
    }

    @Named("toConditionDtoList")
    default List<ConditionResDto> toConditionDtoList(List<Condition> conditions) {
        return conditions.stream()
            .map(c -> ConditionResDto.builder()
                .code(c.getCode())
                .build())
            .collect(Collectors.toList());
    }
}
