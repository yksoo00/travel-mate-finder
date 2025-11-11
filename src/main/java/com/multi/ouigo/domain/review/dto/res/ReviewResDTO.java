package com.multi.ouigo.domain.review.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResDTO {
    private Long id;
    private Long touristId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String nickNm;
    private String  profImg;
}
