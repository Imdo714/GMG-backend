package com.gmg.api.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {

    RUNNING("런닝"),
    BEER("맥주"),
    STUDY("스터디"),
    TRAVEL("여행"),
    ;

    private final String text;

}
