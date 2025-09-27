package com.gmg.api.review.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewCommentDto {

    @NotBlank(message = "리뷰를 입력해주세요.")
    @Size(max = 2000, message = "내용은 2000자를 초과할 수 없습니다.")
    private String comment;
}
