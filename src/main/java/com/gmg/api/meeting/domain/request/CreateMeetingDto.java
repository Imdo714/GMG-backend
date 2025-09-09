package com.gmg.api.meeting.domain.request;

import com.gmg.api.meeting.domain.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class CreateMeetingDto {

    private String title;
    private String content;
    private Category category;
    private LocalDate date;
    private LocalTime time;
    private String address;
    private String addressDetail;
    private Integer personCount;

}
