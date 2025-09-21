package com.gmg.api.meeting.domain.request;

import com.gmg.api.type.Category;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class CreateMeetingDto {

    /**
     * @NotNull: null 값 허용 안 함 (Enum 타입 등 객체에 사용)
     * @NotBlank: null, "", " " 모두 허용 안 함
     * @Size: 문자열 길이 검증 (예: 최대 100자)
     * @FutureOrPresent: 오늘이거나 미래의 날짜만 허용
     */
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max = 2000, message = "내용은 2000자를 초과할 수 없습니다.")
    private String content;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Category category;

    @NotNull(message = "날짜를 입력해주세요.")
    @FutureOrPresent(message = "오늘 또는 미래의 날짜를 선택해주세요.")
    private LocalDate date;

    @NotNull(message = "시간을 입력해주세요.")
    private LocalTime time;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    @Size(max = 255, message = "상세 주소는 255자를 초과할 수 없습니다.")
    private String addressDetail;

    @NotNull(message = "인원 수를 입력해주세요.")
    @Min(value = 2, message = "최소 인원은 2명입니다.")
    private Integer personCount;

}
