package com.multi.ouigo.domain.tourist.repository;

import com.multi.ouigo.domain.tourist.entity.TouristSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TouristSpotRepository extends JpaRepository<TouristSpot,Long>, JpaSpecificationExecutor<TouristSpot> {

}
