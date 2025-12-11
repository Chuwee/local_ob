package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.util.stream.Stream;

public enum BlockingReasonCode {
    STANDARD(0), SOCIAL_DISTANCING(1), RELOCATION(2);

    private final Integer code;

    private BlockingReasonCode(Integer code) {
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
