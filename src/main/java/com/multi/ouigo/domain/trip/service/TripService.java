package com.multi.ouigo.domain.trip.service;

import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.member.repository.MemRepository;
import com.multi.ouigo.domain.trip.dto.req.TripReqDto;
import com.multi.ouigo.domain.trip.dto.res.TripListResDto;
import com.multi.ouigo.domain.trip.dto.res.TripResDto;
import com.multi.ouigo.domain.trip.entity.Trip;
import com.multi.ouigo.domain.trip.mapper.TripMapper;
import com.multi.ouigo.domain.trip.repository.TripRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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


    // 여행 일정 목록 조회

    public List<TripListResDto> getTripList(Long memberNo) {

        log.info("여행 일정 목록 조회 - memberNo: {}", memberNo);

        List<Trip> trips = tripRepository.findByMemberNoOrderByStartDateDesc(memberNo);

        return trips.stream()
                .map(trip -> {
                    TripListResDto dto = tripMapper.toTripListResDto(trip);

                    // D-day 형식
                    long dDayValue = trip.getDday();
                    String formattedDDay;

                    if (dDayValue > 0) {
                        formattedDDay = "D-" + dDayValue;
                    } else if (dDayValue == 0) {
                        formattedDDay = "D-Day";
                    } else {
                        formattedDDay = "D+" + Math.abs(dDayValue);
                    }

                    return TripListResDto.builder()
                            .id(dto.getId())
                            .destination(dto.getDestination())
                            .title(dto.getTitle())
                            .startDate(dto.getStartDate())
                            .endDate(dto.getEndDate())
                            .duration(dto.getDuration())
                            .budget(dto.getBudget())
                            .dDay(formattedDDay)
                            .build();
                })
                .collect(Collectors.toList());
    }


    // 여행 일정 수정
    @Transactional
    public TripResDto updateTrip(Long tripId, TripReqDto tripReqDto) {

        log.info("여행 일정 수정 - tripId: {}", tripId);

        // 일정 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 여행 일정입니다."));

        // 날짜 유효성 검증
        if (tripReqDto.getEndDate().isBefore(tripReqDto.getStartDate())) {
            throw new IllegalArgumentException("종료일은 시작일보다 이후여야 합니다.");
        }

        // 정보 수정
        trip.update(
                tripReqDto.getDestination(),
                tripReqDto.getTitle(),
                tripReqDto.getStartDate(),
                tripReqDto.getEndDate(),
                tripReqDto.getBudget(),
                tripReqDto.getMemo()
        );

        log.info("여행 일정 수정 완료 - tripId: {}", tripId);

        return tripMapper.toTripResDto(trip);
    }


    // 여행 일정 삭제
    @Transactional
    public void deleteTrip(Long tripId) {

        log.info("여행 일정 삭제 - tripId: {}", tripId);

        // 일정 존재 확인
        if (!tripRepository.existsById(tripId)) {
            throw new IllegalArgumentException("존재하지 않는 여행 일정입니다.");
        }

        // 삭제
        tripRepository.deleteById(tripId);

        log.info("여행 일정 삭제 완료 - tripId: {}", tripId);
    }
}
