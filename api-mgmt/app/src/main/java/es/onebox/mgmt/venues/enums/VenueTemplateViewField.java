package es.onebox.mgmt.venues.enums;

import java.util.stream.Stream;

public enum VenueTemplateViewField {

    ROOT("root");

    private String code;

    VenueTemplateViewField(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static VenueTemplateViewField getByCode(String code) {
        return Stream.of(VenueTemplateViewField.values())
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}
