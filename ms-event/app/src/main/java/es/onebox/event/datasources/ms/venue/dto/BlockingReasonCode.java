package es.onebox.event.datasources.ms.venue.dto;

import java.util.stream.Stream;

public enum BlockingReasonCode {
    STANDARD(0), SOCIAL_DISTANCING(1);

    private final Integer code;

    BlockingReasonCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static BlockingReasonCode byCode(Integer code) {
        return Stream.of(BlockingReasonCode.values())
                .filter(v -> v.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public static BlockingReasonCode byName(String name) {
        return Stream.of(BlockingReasonCode.values())
                .filter(v -> v.name().equals(name))
                .findFirst()
                .orElse(null);
    }
}
