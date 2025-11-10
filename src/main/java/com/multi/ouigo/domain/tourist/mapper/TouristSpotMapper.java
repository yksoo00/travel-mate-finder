package com.multi.ouigo.domain.tourist.mapper;
import com.multi.ouigo.domain.tourist.dto.req.TouristSpotReqDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotAllResDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotResDto;
import com.multi.ouigo.domain.tourist.entity.TouristSpot;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TouristSpotMapper {

    // Entity -> ResDto 바로 변환
    TouristSpotResDto toResDto(TouristSpot entity);

    // Entity -> AllResDto
    TouristSpotAllResDto toAllResDto(TouristSpot entity);

    TouristSpot toEntity(TouristSpotReqDto touristSpotReqDto);
}
