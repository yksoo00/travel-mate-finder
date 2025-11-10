package com.multi.ouigo.domain.trip.service;

import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.member.repository.MemRepository;
import com.multi.ouigo.domain.trip.dto.req.TripReqDto;
import com.multi.ouigo.domain.trip.dto.res.TripResDto;
import com.multi.ouigo.domain.trip.entity.Trip;
import com.multi.ouigo.domain.trip.mapper.TripMapper;
import com.multi.ouigo.domain.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripService {

    private final MemRepository memRepository;
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    // 여행 일정 등록

    @Transactional
    public TripResDto createTrip(Long memberNo, TripReqDto tripReqDto) {

        log.info("여행 일정 등록 - memberNo: {}, destination: {}", memberNo, tripReqDto.getDestination());

        // 회원 조회
        Member member = memRepository.findByNo(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 날짜 유효성 검증
        if (tripReqDto.getEndDate().isBefore(tripReqDto.getStartDate())) {
            throw new IllegalArgumentException("종료일은 시작일보다 이후여야 합니다.");
        }

        // Trip 엔티티 생성
        Trip trip = Trip.builder()
                .member(member)
                .destination(tripReqDto.getDestination())
                .title(tripReqDto.getTitle())
                .startDate(tripReqDto.getStartDate())
                .endDate(tripReqDto.getEndDate())
                .budget(tripReqDto.getBudget())
                .memo(tripReqDto.getMemo())
                .build();

        // 저장
        Trip saveTrip = tripRepository.save(trip);

        log.info("여행 일정 등록 완료 - tripId: {}", saveTrip.getId());

        return tripMapper.toTripResDto(saveTrip);
    }
}
