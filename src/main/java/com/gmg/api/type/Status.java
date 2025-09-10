package com.gmg.api.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("거절")
    ;

    private final String text;

    }
