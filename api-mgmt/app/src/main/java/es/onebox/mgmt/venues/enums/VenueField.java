package es.onebox.mgmt.venues.enums;

import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum VenueField implements FiltrableField {

    NAME("name"),
    STATUS("status"),
    CITY("city"),
    IDCOUNTRY("countryId"),
    COUNTRYCODE("countryCode");

    String dtoName;

    VenueField(String dtoName) {
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static VenueField byName(String name) {
        return Stream.of(VenueField.values())
                .filter(v -> v.dtoName.equals(name))
                .findFirst()
                .orElse(null);
    }

}
