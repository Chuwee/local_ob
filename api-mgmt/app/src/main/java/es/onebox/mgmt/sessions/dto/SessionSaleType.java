package es.onebox.mgmt.sessions.dto;

import java.io.Serializable;
import java.util.stream.Stream;

public enum SessionSaleType implements Serializable {

    INDIVIDUAL(1),
    GROUP(2),
    MIXED(3);

    private final Integer id;

    SessionSaleType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static SessionSaleType byId(Integer id) {
        return Stream.of(SessionSaleType.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
