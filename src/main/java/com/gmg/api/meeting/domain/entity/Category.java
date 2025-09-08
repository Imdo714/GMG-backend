package com.gmg.api.meeting.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {

    ALL("전체 메뉴"),
    RUNNING("런닝"),
    BEER("맥주"),
    STUDY("스터디"),
    TRAVEL("여행"),
    ;

    private final String text;

}
