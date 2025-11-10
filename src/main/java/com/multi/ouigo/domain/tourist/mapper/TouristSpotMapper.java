package com.multi.ouigo.domain.tourist.mapper;

import com.multi.ouigo.domain.tourist.dto.req.TouristSpotReqDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotResDto;
import com.multi.ouigo.domain.tourist.entity.TouristSpotEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface TouristSpotMapper {
    //req -> dto
    TouristSpotEntity  toEntity(TouristSpotReqDto reqDto);

    // Entity -> ResDto 바로 변환
    TouristSpotResDto toResDto(TouristSpotEntity entity);

}
