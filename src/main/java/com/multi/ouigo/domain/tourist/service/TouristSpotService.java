package com.multi.ouigo.domain.tourist.service;


import com.multi.ouigo.domain.tourist.dto.res.TouristSpotAllResDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotResDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TouristSpotService {
    Page<TouristSpotResDto> getTouristSpots(Pageable pageable);

    TouristSpotAllResDto getTouristSpotById(Long id);
}
