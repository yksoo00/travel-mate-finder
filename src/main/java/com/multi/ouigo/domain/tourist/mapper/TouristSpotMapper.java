package com.multi.ouigo.domain.tourist.mapper;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotAllResDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotResDto;
import com.multi.ouigo.domain.tourist.entity.TouristSpotEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TouristSpotMapper {

    // Entity -> ResDto 바로 변환
    TouristSpotResDto toResDto(TouristSpotEntity entity);

    // Entity -> AllResDto
    TouristSpotAllResDto toAllResDto(TouristSpotEntity entity);
}
