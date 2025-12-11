package es.onebox.mgmt.venues.enums;

import java.util.stream.Stream;

public enum VenueTemplateSeatField {

    CONTAINER_ID("container_id"),
    CONTAINER_NAME("container_name"),
    SECTOR_NAME("sector_name"),
    ROW("row"),
    SEAT("seat"),
    SEAT_ID("seat_id");

    private String code;

    VenueTemplateSeatField(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static VenueTemplateSeatField getByCode(String code) {
        return Stream.of(VenueTemplateSeatField.values())
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}
