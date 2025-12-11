package es.onebox.event.sessions.dto;

import java.util.stream.Stream;

public enum SessionSalesType {

    INDIVIDUAL(1),
    GROUP(2),
    MIXED(3);

    private int type;

    SessionSalesType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static SessionSalesType byId(Integer id) {
        return Stream.of(SessionSalesType.values())
                .filter(v -> v.getType() == id)
                .findFirst()
                .orElse(null);
    }
}
