package com.multi.ouigo.domain.trip.service;

import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.member.repository.MemRepository;
import com.multi.ouigo.domain.trip.dto.res.TripCardResDto;
import com.multi.ouigo.domain.trip.entity.Trip;
import com.multi.ouigo.domain.trip.entity.TripCard;
import com.multi.ouigo.domain.trip.mapper.TripCardMapper;
import com.multi.ouigo.domain.trip.repository.TripCardRepository;
import com.multi.ouigo.domain.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripCardService {

    private final TripCardRepository tripCardRepository;
    private final TripRepository tripRepository;
    private final MemRepository memRepository;
    private final TripCardMapper tripCardMapper;

    //여행 카드 통계 조회 (실시간 계산 후 업데이트)

    @Transactional
    public TripCardResDto getTripCard(Long memberNo) {

        log.info("여행 카드 조회 - memberNo: {}", memberNo);

        // 회원 조회
        Member member = memRepository.findByNo(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 회원의 모든 여행 일정 조회
        List<Trip> trips = tripRepository.findByMemberNoOrderByStartDateDesc(memberNo);

        if (trips.isEmpty()) {
            throw new IllegalArgumentException("여행 일정을 찾을 수 없습니다.");
        }

        // 실시간 통계 계산
        int dstcCnt = trips.size();
        int ttlCnt = trips.size();
        int dayCnt = 0;

        for (Trip trip : trips) {
            dayCnt += trip.getDuration();  // 각 여행의 일수 누적
        }

        log.info("통계 계산 완료 - 지역: {}, 관광지: {}, 일수: {}", dstcCnt, ttlCnt, dayCnt);

        // TripCard 조회
        TripCard tripCard = tripCardRepository.findByMemberNo(memberNo)
                .orElseGet(() -> {
                    log.info("여행 카드 신규 생성 - memberNo: {}", memberNo);


                    return TripCard.builder()
                            .member(member)
                            .destinationCount(0)
                            .titleCount(0)
                            .dayCount(0)
                            .build();
                });

        // 통계 업데이트
        tripCard.updateStatistics(dstcCnt, ttlCnt, dayCnt);

        // 저장
        TripCard newCard = tripCardRepository.save(tripCard);

        log.info("여행 카드 조회 완료 - memberNo: {}", memberNo);

        // DTO 변환 후 반환
        return tripCardMapper.toTripCardResDto(newCard);
    }
}

