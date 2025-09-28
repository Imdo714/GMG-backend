package com.gmg.api.review.domain.response;

import com.gmg.api.review.domain.response.dto.RevieweeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@AllArgsConstructor
@Builder
public class RevieweeListResponse {

    private List<RevieweeDto> list;
    private Integer count;
}
