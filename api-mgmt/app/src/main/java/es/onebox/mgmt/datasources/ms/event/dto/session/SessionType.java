package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.util.stream.Stream;

public enum SessionType {
    SESSION(0),
    SEASON_RESTRICTIVE(1),
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
