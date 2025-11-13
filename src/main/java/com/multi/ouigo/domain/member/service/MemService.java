package com.multi.ouigo.domain.member.service;

import com.multi.ouigo.common.exception.custom.NotFindException;
import com.multi.ouigo.common.jwt.provider.TokenProvider;
import com.multi.ouigo.domain.member.dto.res.MemProfResDto;
import com.multi.ouigo.domain.member.dto.res.MemResDto;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.member.mapper.MemMapper;
import com.multi.ouigo.domain.member.repository.MemRepository;
import com.multi.ouigo.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final MemRepository memRepository;
    private final MemMapper memMapper;
    @Value("${profileimg}")
    private String profileimg;

    // 마이페이지 - 전체 정보 조회

    public MemProfResDto getMemberProfile(HttpServletRequest request) {

        String memberId = tokenProvider.extractMemberId(request);
        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotFindException("없는 멤버입니다"));

        return memMapper.memProfResDto(member);

    }

    // 마이페이지 - 기본 정보 수정

    @Transactional
    public MemResDto updateMemberProfile(HttpServletRequest request, MemResDto memResDto) {

        // 회원 조회
        String memberId = tokenProvider.extractMemberId(request);
        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotFindException("없는 멤버입니다"));

        // 정보 수정
        member.update(memResDto.getNickName(), memResDto.getEmail(), memResDto.getProfileImage(),
            memResDto.getIntroduction());

        // 저장
        return memMapper.toMemResDto(member);
    }


    // 기본 정보 수정 - 프로필 사진 수정
    @Transactional
    public String uploadProfileImage(HttpServletRequest request, MultipartFile file) {

        String memberId = tokenProvider.extractMemberId(request);
        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotFindException("없는 멤버입니다"));

        // 파일 유효성 검증
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }

        // 파일명 생성 (중복 방지)
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // 파일 저장
        try {

            // 저장 경로 생성
            Path uploadPath = Paths.get(profileimg);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            Path filePath = uploadPath.resolve(uniqueFilename);
            file.transferTo(filePath.toFile());

            // URL 생성
            String imageUrl = "/uploads/profiles/" + uniqueFilename;

            // DB 업데이트
            member.update(
                member.getNickName(),
                member.getEmail(),
                member.getProfileImage(),
                member.getIntroduction()
            );

            log.info("프로필 이미지 업로드 완료 - URL: {}", imageUrl);

            return imageUrl;

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드에 실패했습니다.");
        }
    }
}


