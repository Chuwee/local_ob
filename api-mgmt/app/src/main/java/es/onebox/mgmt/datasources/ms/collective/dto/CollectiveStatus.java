package es.onebox.mgmt.datasources.ms.collective.dto;

import java.util.stream.Stream;

public enum CollectiveStatus {

    ACTIVE(1),
    INACTIVE(2),
    DELETED(0);

    private Integer id;

    private CollectiveStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static CollectiveStatus fromId(Integer id) {
        return Stream.of(values())
                .filter(p -> p.id.equals(id))
                .findAny()
                .orElse(null);
    }
}
