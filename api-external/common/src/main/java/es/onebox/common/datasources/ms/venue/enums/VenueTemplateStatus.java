package es.onebox.common.datasources.ms.venue.enums;

import java.util.stream.Stream;

public enum VenueTemplateStatus {

    DELETED(0),
    ACTIVE(1),
    IN_PROGRESS(2),
    ERROR(3);

    private final Integer id;

    VenueTemplateStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static VenueTemplateStatus byId(Integer id) {
        return Stream.of(VenueTemplateStatus.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
