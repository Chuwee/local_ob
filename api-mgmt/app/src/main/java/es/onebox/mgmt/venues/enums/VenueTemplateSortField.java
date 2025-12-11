package es.onebox.mgmt.venues.enums;

import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum VenueTemplateSortField implements FiltrableField {

    NAME("name"),
    STATUS("status"),
    TYPE("type"),
    ENTITY("entity_name"),
    VENUE_ENTITY("venue_entity_name"),
    VENUE("venue_name"),
    CITY("venue_city"),
    GRAPHIC("graphic");

    private String dtoName;

    VenueTemplateSortField(String dtoName) {
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static VenueTemplateSortField byName(String name) {
        return Stream.of(VenueTemplateSortField.values())
                .filter(v -> v.dtoName.equals(name))
                .findFirst()
                .orElse(null);
    }
}
