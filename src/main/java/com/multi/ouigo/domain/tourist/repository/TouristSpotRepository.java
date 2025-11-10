package com.multi.ouigo.domain.tourist.repository;

import com.multi.ouigo.domain.tourist.entity.TouristSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TouristSpotRepository extends JpaRepository<TouristSpotEntity,Long>, JpaSpecificationExecutor<TouristSpotEntity> {

}
