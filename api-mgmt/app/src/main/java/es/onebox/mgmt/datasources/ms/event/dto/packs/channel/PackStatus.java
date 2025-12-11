package es.onebox.mgmt.datasources.ms.event.dto.packs.channel;

import java.util.stream.Stream;

public enum PackStatus {
    ACTIVE(1),
    INACTIVE(2),
    DELETED(0);

    private Integer id;

    PackStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public static PackStatus getById(final Integer id) {
        return Stream.of(values()).filter(field -> field.getId().equals(id)).findFirst().orElse(null);
    }
}
