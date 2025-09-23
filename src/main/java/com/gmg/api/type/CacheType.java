package com.gmg.api.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum CacheType {

    PROGRAMS("meetingDetailCache", 60, 20);

    CacheType(String cacheName, int expiredAfterWrite, int maximumSize) {
        this.cacheName = cacheName;
        this.expiredAfterWrite = expiredAfterWrite;
        this.maximumSize = maximumSize;
    }

    private String cacheName;
    private int expiredAfterWrite;
    private int maximumSize;
}
