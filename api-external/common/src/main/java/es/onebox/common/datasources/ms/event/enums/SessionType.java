package es.onebox.common.datasources.ms.event.enums;

import java.util.stream.Stream;

public enum SessionType {

    SESSION(0),
    RESTRICTED_PACK(1),
    SEASON_RESTRICTIVE(1),
    UNRESTRICTED_PACK(2),
    SEASON_FREE(2);

    private Integer type;

    SessionType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static SessionType getById(Integer id) {
        return Stream.of(values()).filter(el -> el.type.equals(id)).findFirst().orElse(null);
    }

}
