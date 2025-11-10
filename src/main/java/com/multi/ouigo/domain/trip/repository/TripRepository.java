package com.multi.ouigo.domain.trip.repository;

import com.multi.ouigo.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // 특정 회원(MemberNo)의 모든 일정 조회(최신순)

    List<Trip> findByMemberNoOrderByStartDateDesc(Long memberNo);
}
