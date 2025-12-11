package es.onebox.mgmt.venues.enums;

import java.util.stream.Stream;

public enum VenueTemplateSectorField {

    CODE("code"),
    NAME("name");

    private String code;

    VenueTemplateSectorField(String dtoName) {
        this.code = dtoName;
    }

    public String getCode() {
        return code;
    }

    public static VenueTemplateSectorField getByCode(String code) {
        return Stream.of(VenueTemplateSectorField.values())
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}
