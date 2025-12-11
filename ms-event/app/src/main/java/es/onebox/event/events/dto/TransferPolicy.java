package es.onebox.event.events.dto;

import java.util.Arrays;

public enum TransferPolicy {
    ALL(0),
    FRIENDS_AND_FAMILY(1);

    private final int id;

    TransferPolicy(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TransferPolicy getById(final int id) {
        return Arrays.stream(values())
                .filter(policy -> policy.id == id)
                .findFirst()
                .orElse(null);
    }
}
