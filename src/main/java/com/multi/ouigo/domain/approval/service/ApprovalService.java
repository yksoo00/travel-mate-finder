package com.multi.ouigo.domain.approval.service;

import com.multi.ouigo.common.exception.custom.NotAuthorizedException;
import com.multi.ouigo.common.exception.custom.NotFindException;
import com.multi.ouigo.common.jwt.provider.TokenProvider;
import com.multi.ouigo.domain.approval.constant.ApprovalStatus;
import com.multi.ouigo.domain.approval.dto.req.ApprovalReqDto;
import com.multi.ouigo.domain.approval.dto.res.ApprovalMyRecruitResDto;
import com.multi.ouigo.domain.approval.entity.Approval;
import com.multi.ouigo.domain.approval.mapper.ApprovalMapper;
import com.multi.ouigo.domain.approval.repository.ApprovalRepository;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.member.repository.MemberRepository;
import com.multi.ouigo.domain.recruit.entity.Recruit;
import com.multi.ouigo.domain.recruit.repository.RecruitRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j

@RequiredArgsConstructor
public class ApprovalService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final ApprovalRepository approvalRepository;
    private final ApprovalMapper approvalMapper;
    private final RecruitRepository recruitRepository;

    public Page<ApprovalMyRecruitResDto> findAllMyRecruitApproval(HttpServletRequest request,
        Pageable pageable) {
        String memberId = tokenProvider.extractMemberId(request);

        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotFindException("없는 멤버입니다"));

        Recruit recruit = recruitRepository.findByMember_No(member.getNo())
            .orElseThrow(() -> new NotFindException("모집 글을 찾을 수 없습니다"));

        Page<Approval> recruits = approvalRepository.findAllByRecruitId(recruit.getId(),
            pageable);

        return recruits.map(approvalMapper::toMyRecruitDto);
    }

    public Page<ApprovalMyRecruitResDto> findAllMyApproval(HttpServletRequest request,
        Pageable pageable) {
        String memberId = tokenProvider.extractMemberId(request);

        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotFindException("없는 멤버입니다"));

        Page<Approval> recruits = approvalRepository.findAllByMemberNo(member.getNo(),
            pageable);

        return recruits.map(approvalMapper::toMyRecruitDto);
    }

    @Transactional
    public void approveRecruit(HttpServletRequest request, ApprovalReqDto approvalReqDto) {
        String memberId = tokenProvider.extractMemberId(request);

        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotFindException("없는 멤버입니다"));

        Recruit recruit = recruitRepository.findById(approvalReqDto.getRecruitId())
            .orElseThrow(() -> new NotFindException("모집 글을 찾을 수 없습니다"));

        if (!recruit.getMember().getNo().equals(member.getNo())) {
            throw new NotAuthorizedException("본인 모집글만 상태르 변경할 수 있습니다");
        }

        Approval approval = recruit.getApprovals().stream()
            .filter(a -> a.getMember().getNo().equals(approvalReqDto.getMemberNo()))
            .findFirst()
            .orElseThrow(() -> new NotFindException("신청자를 찾을 수 없습니다"));

        approval.setStatus(ApprovalStatus.APPROVED);

    }

    @Transactional
    public void rejectedRecruit(HttpServletRequest request, ApprovalReqDto approvalReqDto) {
        String memberId = tokenProvider.extractMemberId(request);

        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotFindException("없는 멤버입니다"));

        Recruit recruit = recruitRepository.findById(approvalReqDto.getRecruitId())
            .orElseThrow(() -> new NotFindException("모집 글을 찾을 수 없습니다"));

        if (!recruit.getMember().getNo().equals(member.getNo())) {
            throw new NotAuthorizedException("본인 모집글만 상태르 변경할 수 있습니다");
        }

        Approval approval = recruit.getApprovals().stream()
            .filter(a -> a.getMember().getNo().equals(approvalReqDto.getMemberNo()))
            .findFirst()
            .orElseThrow(() -> new NotFindException("신청자를 찾을 수 없습니다"));

        approval.setStatus(ApprovalStatus.REJECTED);
    }


}
