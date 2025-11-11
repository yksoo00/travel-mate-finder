package com.multi.ouigo.domain.trip.repository;


import com.multi.ouigo.domain.trip.entity.TripCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TripCardRepository extends JpaRepository<TripCard, Long> {

    //특정 회원의 여행 카드 조회

    Optional<TripCard> findByMemberNo(Long memberNo);

}