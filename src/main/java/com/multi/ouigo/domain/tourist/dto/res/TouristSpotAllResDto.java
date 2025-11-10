package com.multi.ouigo.domain.tourist.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TouristSpotAllResDto {
    private Long id;
    private String district;
    private String title;
    private String description;
    private String address;
    private String phone;
}
