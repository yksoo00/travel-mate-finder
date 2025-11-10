package com.multi.ouigo.domain.recruit.service;

import com.multi.ouigo.common.exception.custom.NotFindException;
import com.multi.ouigo.common.jwt.provider.TokenProvider;
import com.multi.ouigo.domain.condition.entity.Condition;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.member.repository.MemberRepository;
import com.multi.ouigo.domain.recruit.dto.req.CreateRecruitReqDto;
import com.multi.ouigo.domain.recruit.entity.Recruit;
import com.multi.ouigo.domain.recruit.mapper.RecruitMapper;
import com.multi.ouigo.domain.recruit.repository.RecruitRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecruitService {

    private final TokenProvider tokenProvider;

    private final MemberRepository memberRepository;

    private final RecruitRepository recruitRepository;

    private final RecruitMapper recruitMapper;

    @Transactional
    public Recruit createRecruit(HttpServletRequest request,
        CreateRecruitReqDto createRecruitReqDto) {
        String memberId = tokenProvider.extractMemberId(request);
        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotFindException("없는 멤버입니다"));

        Recruit recruit = recruitMapper.toEntity(createRecruitReqDto);
        recruit.setMember(member);

        addConditions(recruit, createRecruitReqDto.getGenderCodes());
        addConditions(recruit, createRecruitReqDto.getAgeCodes());
        return recruitRepository.save(recruit);

    }

    private void addConditions(Recruit recruit, List<? extends Enum<?>> codes) {
        if (codes == null) {
            return;
        }
        for (Enum<?> code : codes) {
            Condition condition = Condition.builder()
                .code(code.name())
                .build();
            recruit.addCondition(condition);
        }
    }
}
