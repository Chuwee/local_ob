package es.onebox.flc.orders.dto.groups;

import java.util.stream.Stream;

public enum AttributeScopes {
    EVENT(0),
    SESSION(1),
    GROUP(2),
    PROFILE(3);

    private Integer id;

    private AttributeScopes(int id) {
        this.id = id;
    }

    public static AttributeScopes get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }

    public static AttributeScopes byId(Integer id) {
        return Stream.of(AttributeScopes.values())
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
