package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.util.stream.Stream;

public enum VenueTemplateScope {
    VENUE(1),
    CAPACITIES(2),
    EVENT(3);

    private final Integer id;

    VenueTemplateScope(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static VenueTemplateScope byId(Integer id) {
        return Stream.of(VenueTemplateScope.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
