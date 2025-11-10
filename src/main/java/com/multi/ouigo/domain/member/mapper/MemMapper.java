package com.multi.ouigo.domain.member.mapper;

import com.multi.ouigo.domain.member.dto.res.MemProfResDto;
import com.multi.ouigo.domain.member.dto.res.MemResDto;
import com.multi.ouigo.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemMapper {

    // MemberEntity → MemberResponse 매핑

    @Mapping(source = "no", target = "no")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "nickName", target = "nickName")
    @Mapping(source = "profileImage", target = "profileImage")
    @Mapping(source = "introduction", target = "introduction")
    MemResDto toMemResDto(Member member);

    @Mapping(source = "no", target = "no")
    @Mapping(source = "nickName", target = "nickName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "profileImage", target = "profileImage")
    @Mapping(source = "introduction", target = "introduction")
    @Mapping(source = "age", target = "age")
    @Mapping(source = "gender", target = "gender")
    MemProfResDto memProfResDto(Member entity);
}
