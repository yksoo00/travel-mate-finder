package com.multi.ouigo.domain.tourist.service;


import com.multi.ouigo.domain.tourist.dto.req.TouristSpotReqDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotAllResDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotResDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TouristSpotService {

    Page<TouristSpotResDto> getTouristSpots(String keyword, Pageable pageable);
    List<TouristSpotResDto> getTouristSpots(String keyword);
    TouristSpotAllResDto getTouristSpotById(Long id);

    Long save(TouristSpotReqDto touristSpotReqDto);

    void updateById(Long id,TouristSpotReqDto touristSpotReqDto);

    void deleteById(Long id);

    String getImages(String keyword);
}
