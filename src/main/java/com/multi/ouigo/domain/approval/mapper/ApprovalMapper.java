package com.multi.ouigo.domain.approval.mapper;

import com.multi.ouigo.domain.approval.dto.res.ApprovalMyRecruitResDto;
import com.multi.ouigo.domain.approval.entity.Approval;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApprovalMapper {

    @Mapping(target = "approvalId", source = "id") // ✅ 추가
    @Mapping(target = "memberNo", expression = "java(approval.getMember().getNo())")
    @Mapping(target = "memberNickName", expression = "java(approval.getMember().getNickName())")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "recruitId", expression = "java(approval.getRecruit().getId())")
    @Mapping(target = "touristSpot", expression = "java(approval.getRecruit().getTouristSpot().getTitle())")
    ApprovalMyRecruitResDto toMyRecruitDto(Approval approval);
}
