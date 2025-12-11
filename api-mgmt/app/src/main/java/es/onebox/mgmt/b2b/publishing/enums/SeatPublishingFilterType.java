package es.onebox.mgmt.b2b.publishing.enums;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;

import java.util.stream.Stream;

public enum SeatPublishingFilterType {

    EVENT("events");

    private final String key;

    SeatPublishingFilterType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static void validateFilter(String key) {
        if (Stream.of(values()).noneMatch(it -> it.key.equals(key))) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.FILTER_NOT_FOUND, key);
        }
    }
}