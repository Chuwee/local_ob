package es.onebox.mgmt.users.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum UserStatus implements Serializable {

    ACTIVE(1),
    PENDING(2),
    BLOCKED(3),
    TEMPORARY_BLOCKED(4);

    private final Integer id;

    UserStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static UserStatus byId(Integer id) {
        return Stream.of(UserStatus.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
