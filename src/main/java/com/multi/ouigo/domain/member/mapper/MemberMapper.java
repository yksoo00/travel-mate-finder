package com.multi.ouigo.domain.member.mapper;


import com.multi.ouigo.domain.member.dto.req.MemberSignUpDto;
import com.multi.ouigo.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// @Mapper 를 선언해 mapper 임을 명시 //componentModel = "spring" 은 spring Container 에 Bean 으로 등록하는 설정
// (componentModel = "spring" 를 추가하지 않으면 Bean 으로 등록되지 않으므로 주의)

@Mapper(componentModel = "spring")
public interface MemberMapper {

    // Product → ProductResAllDto 매핑
    @Mapping(source = "memberPassword", target = "password")
    @Mapping(source = "memberEmail", target = "email")
    @Mapping(source = "memberName", target = "nickName")
    @Mapping(source = "memberAge", target = "age")
    @Mapping(source = "memberGender", target = "gender")
    @Mapping(source = "memberIntro", target = "introduction")
    Member toEntity(MemberSignUpDto memberSignUpDto);


}
