package com.multi.ouigo.domain.trip.mapper;

import com.multi.ouigo.domain.trip.dto.res.TripListResDto;
import com.multi.ouigo.domain.trip.dto.res.TripResDto;
import com.multi.ouigo.domain.trip.entity.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripMapper {

    //Trip Entity → TripResDto (등록, 수정용) : 필드명 = 엔티티명 똑같아서 생략

    TripResDto toTripResDto(Trip trip);



    // Trip Entity → TripListResDto (목록 조회용)

    @Mapping(target = "dDay", ignore = true)
    TripListResDto toTripListResDto(Trip trip);
}
