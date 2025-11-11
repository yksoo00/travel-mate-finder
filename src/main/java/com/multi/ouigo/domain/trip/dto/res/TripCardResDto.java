package com.multi.ouigo.domain.trip.dto.res;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripCardResDto {

    private Integer dstcCnt;
    private Integer ttlCnt;
    private Integer dayCnt;
}
