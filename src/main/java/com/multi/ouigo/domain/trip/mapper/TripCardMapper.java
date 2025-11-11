package com.multi.ouigo.domain.trip.mapper;

import com.multi.ouigo.domain.trip.dto.res.TripCardResDto;
import com.multi.ouigo.domain.trip.entity.TripCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripCardMapper {

    // TripCard Entity → TripCardResDto

    @Mapping(source = "destinationCount", target = "dstcCnt")
    @Mapping(source = "titleCount", target = "ttlCnt")
    @Mapping(source = "dayCount", target = "dayCnt")
    TripCardResDto toTripCardResDto(TripCard tripCard);
}
