package es.onebox.mgmt.venues.enums;

import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum VenueTemplateFilterField implements FiltrableField {

    NAME("name"),
    TYPE("type");

    private String dtoName;

    VenueTemplateFilterField(String dtoName) {
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static VenueTemplateFilterField byName(String name) {
        return Stream.of(VenueTemplateFilterField.values())
                .filter(v -> v.dtoName.equals(name))
                .findFirst()
                .orElse(null);
    }
}
