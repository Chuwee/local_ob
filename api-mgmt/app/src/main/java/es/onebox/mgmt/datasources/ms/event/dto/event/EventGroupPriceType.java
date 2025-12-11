package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.util.stream.Stream;

public enum EventGroupPriceType {

    FIXED(1),
    INDIVIDUAL(2);

    private final Integer id;

    EventGroupPriceType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static Integer getIdByName(String name) {
        return Stream.of(EventGroupPriceType.values())
                .filter(v -> v.name().equals(name))
                .map(EventGroupPriceType::getId)
                .findFirst()
                .orElse(null);
    }

}
