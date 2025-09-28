package com.gmg.api.review.domain.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RevieweeDto {

    private Long reviewId;
    private String comment;
}
