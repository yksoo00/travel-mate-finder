package com.multi.ouigo.domain.tourist.service;

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
//        Page<TouristSpot> touristSpots = touristSpotRepository.findAll(pageable).map(touristSpotEntity -> {
//            TouristSpot touristSpot = touristSpotMapper.toDomain(touristSpotEntity);
//            return touristSpot;
//        });
//        //  **
//        return touristSpots.map(touristSpot -> {
//            TouristSpotResDto resDto = touristSpotMapper.toResDto(touristSpot);
//            return resDto;
//        });
        return touristSpotRepository.findAll(pageable)
                .map(touristSpotMapper::toResDto);
    }
}
