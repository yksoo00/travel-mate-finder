package com.multi.ouigo.domain.tourist.service;

import com.multi.ouigo.domain.tourist.dto.res.TouristSpotAllResDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotResDto;
import com.multi.ouigo.domain.tourist.entity.TouristSpotEntity;
import com.multi.ouigo.domain.tourist.mapper.TouristSpotMapper;
import com.multi.ouigo.domain.tourist.repository.TouristSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class TouristSpotServiceImpl implements TouristSpotService {

    private final TouristSpotRepository touristSpotRepository;
    private final TouristSpotMapper touristSpotMapper;
    @Override
    public Page<TouristSpotResDto> getTouristSpots(Pageable pageable) {
        return touristSpotRepository.findAll(pageable)
                .map(touristSpotMapper::toResDto);
    }

    @Override
    public TouristSpotAllResDto getTouristSpotById(Long id) {
        TouristSpotEntity touristSpot = touristSpotRepository.findById(id).orElseThrow(()->new IllegalArgumentException("조회할 관광지가 존재하지 않습니다."));
        return touristSpotMapper.toAllResDto(touristSpot);
    }
}
