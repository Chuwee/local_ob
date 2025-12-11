package es.onebox.mgmt.datasources.ms.entity.enums;

import java.io.Serializable;
import java.util.Arrays;

public enum EntityStatus implements Serializable {

    DELETED(0),
    ACTIVE(1),
    PENDING(2),
    BLOCKED(3),
    TEMPORARILY_BLOCKED(4);

    private int state;

    private EntityStatus(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public static EntityStatus get(int id) {
        return values()[id];
    }

    public static EntityStatus getById(int id) {
        return Arrays.stream(EntityStatus.values()).filter(s -> s.getState() == id).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return String.valueOf(state);
    }

}
