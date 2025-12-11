package es.onebox.mgmt.entities.enums;

import java.io.Serializable;
import java.util.Arrays;

public enum EntityStatus implements Serializable {

    ACTIVE(1),
    PENDING(2),
    BLOCKED(3),
    TEMPORARY_BLOCKED(4);

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
