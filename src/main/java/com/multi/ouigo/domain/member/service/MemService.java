package com.multi.ouigo.domain.member.service;

import com.multi.ouigo.domain.member.dto.res.MemProfResDto;
import com.multi.ouigo.domain.member.dto.res.MemResDto;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.member.mapper.MemMapper;
import com.multi.ouigo.domain.member.repository.MemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemService {

    private final MemRepository memRepository;
    private final MemMapper memMapper;


    // 마이페이지 - 전체 정보 조회

    public MemProfResDto getMemberProfile(Long memberNo) {

            log.info("회원 정보 조회 - no: {}", memberNo);

            Member member = memRepository.findByNo(memberNo)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

            return memMapper.memProfResDto(member);

    }


    // 마이페이지 - 기본 정보 수정

    @Transactional
    public MemResDto updateMemberProfile(Long memberNo, MemResDto memResDto) {


        // 회원 조회
        Member member = memRepository.findByNo(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));


        // 정보 수정
        member.update(memResDto.getNickname(),memResDto.getEmail(), memResDto.getProfileImage(),memResDto.getIntroduction());


        // 저장
        log.info("회원 정보 수정 완료 - no: {}", memberNo);
        return memMapper.toMemResDto(member);
    }

}


