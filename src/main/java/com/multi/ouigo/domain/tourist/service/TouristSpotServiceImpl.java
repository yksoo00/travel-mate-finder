package com.multi.ouigo.domain.tourist.service;

import com.multi.ouigo.domain.tourist.dto.req.TouristSpotReqDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotAllResDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotResDto;
import com.multi.ouigo.domain.tourist.entity.TouristSpot;
import com.multi.ouigo.domain.tourist.mapper.TouristSpotMapper;
import com.multi.ouigo.domain.tourist.repository.TouristSpotRepository;
import com.multi.ouigo.domain.tourist.specification.TouristSpotSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TouristSpotServiceImpl implements TouristSpotService {

    private final TouristSpotRepository touristSpotRepository;
    private final TouristSpotMapper touristSpotMapper;

    @Override
    public Page<TouristSpotResDto> getTouristSpots(String keyword, Pageable pageable) {
        Specification<TouristSpot> spec = (root, query, cb) -> null;
        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(Specification.anyOf( // anyOf = OR 조건
                    TouristSpotSpecification.titleContains(keyword),
                    TouristSpotSpecification.descriptionContains(keyword),
                    TouristSpotSpecification.addressContains(keyword),
                    TouristSpotSpecification.districtContains(keyword),
                    TouristSpotSpecification.phoneContains(keyword)
            ));
        }
        Page<TouristSpot> page = touristSpotRepository.findAll(spec, pageable);
        return page.map(touristSpotMapper::toResDto);
    }

    @Override
    public TouristSpotAllResDto getTouristSpotById(Long id) {
        TouristSpot touristSpot = touristSpotRepository.findById(id).orElseThrow(()->new IllegalArgumentException("조회할 관광지가 존재하지 않습니다."));
        return touristSpotMapper.toAllResDto(touristSpot);
    }

    @Transactional
    @Override
    public Long save(TouristSpotReqDto touristSpotReqDto) {

        TouristSpot touristSpot = touristSpotMapper.toEntity(touristSpotReqDto);
        return touristSpotRepository.save(touristSpot).getId();
    }

    @Transactional
    @Override
    public void updateById(Long id, @Valid TouristSpotReqDto touristSpotReqDto) {
        TouristSpot touristSpot = touristSpotRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("수정할 데이터가 없음."));

        touristSpot.update(touristSpotReqDto);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        touristSpotRepository.deleteById(id);
    }
}
