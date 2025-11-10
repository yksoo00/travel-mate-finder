package com.multi.ouigo.domain.recruit.service;

import com.multi.ouigo.common.exception.custom.NotFindException;
import com.multi.ouigo.common.jwt.provider.TokenProvider;
import com.multi.ouigo.domain.condition.entity.Condition;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.member.repository.MemRepository;
import com.multi.ouigo.domain.member.repository.MemberRepository;
import com.multi.ouigo.domain.recruit.dto.req.CreateRecruitReqDto;
import com.multi.ouigo.domain.recruit.dto.res.RecruitListResDto;
import com.multi.ouigo.domain.recruit.dto.res.RecruitResDto;
import com.multi.ouigo.domain.recruit.entity.Recruit;
import com.multi.ouigo.domain.recruit.mapper.RecruitMapper;
import com.multi.ouigo.domain.recruit.repository.RecruitRepository;
import com.multi.ouigo.domain.tourist.entity.TouristSpot;
import com.multi.ouigo.domain.tourist.repository.TouristSpotRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    private final TouristSpotRepository touristSpotRepository;

    private final MemRepository memRepository;

    @Transactional
    public Recruit createRecruit(HttpServletRequest request,
        CreateRecruitReqDto createRecruitReqDto) {
        String memberId = tokenProvider.extractMemberId(request);
        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotFindException("없는 멤버입니다"));
        TouristSpot touristSpot = touristSpotRepository.findById(
                createRecruitReqDto.getTouristSpotId())
            .orElseThrow(() -> new NotFindException("관광지를 찾을 수 없습니다"));
        Recruit recruit = recruitMapper.toEntity(createRecruitReqDto);
        recruit.setMember(member);
        recruit.setTouristSpot(touristSpot);

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

    public Page<RecruitListResDto> findAllRecruit(HttpServletRequest request, Pageable pageable,
        String category, LocalDate startDate, LocalDate endDate) {
        String memberId = tokenProvider.extractMemberId(request);
        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotFindException("없는 멤버입니다"));

        Specification<Recruit> spec = (root, query, cb) -> cb.conjunction();

        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("category"), category));
        }

        if (startDate != null) {
            spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("startDate"), startDate));
        }

        if (endDate != null) {
            spec = spec.and((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("endDate"), endDate));
        }

        Page<Recruit> pageResult = recruitRepository.findAll(spec, pageable);

        return pageResult.map(recruitMapper::toDto);

    }

    public RecruitResDto findRecruit(HttpServletRequest request, Long recruitId) {
        String memberId = tokenProvider.extractMemberId(request);
        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotFindException("없는 멤버입니다"));

        Recruit recruit = recruitRepository.findById(recruitId)
            .orElseThrow(() -> new NotFindException("모집 글을 찾을 수 없습니다"));

        TouristSpot touristSpot = touristSpotRepository.findById(recruit.getTouristSpot().getId())
            .orElseThrow(() -> new NotFindException("관광지를 찾을 수 없습니다"));

        Member recruitMember = memRepository.findByNo(recruit.getMember().getNo())
            .orElseThrow(() -> new NotFindException("여행장을 찾을 수 없습니다"));
        
        // 4. Mapper 사용하여 DTO 변환
        return recruitMapper.toResDto(recruit);
    }
}
