package es.onebox.event.sessions.dao.enums;

import java.util.Arrays;

public enum PresaleStatus {

    DELETED (0), ENABLED(1), DISABLED(1);

    private final Integer id;

    PresaleStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PresaleStatus getById(Integer id) {
        return Arrays.stream(values())
                .filter( item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
