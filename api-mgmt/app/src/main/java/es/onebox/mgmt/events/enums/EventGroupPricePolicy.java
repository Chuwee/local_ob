package es.onebox.mgmt.events.enums;

import java.util.stream.Stream;

public enum EventGroupPricePolicy {

    FIXED(1),
    INDIVIDUAL(2);

    EventGroupPricePolicy(int id) {
        this.id = id;
    }

    private Integer id;

    public static EventGroupPricePolicy fromId(Integer id) {
        return Stream.of(EventGroupPricePolicy.values())
                .filter(v -> v.id.equals(id))
                .findFirst()
                .orElse(FIXED);
    }
}
