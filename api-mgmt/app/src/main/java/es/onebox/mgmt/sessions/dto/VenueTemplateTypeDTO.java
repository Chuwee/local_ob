package es.onebox.mgmt.sessions.dto;

import java.util.Arrays;

public enum VenueTemplateTypeDTO {
    NORMAL(1),
    AVET(2),
    ACTIVITY(3);

    private final Integer id;

    VenueTemplateTypeDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static VenueTemplateTypeDTO fromId(Integer id) {
        return Arrays.stream(values())
                .filter(elem -> elem.id.equals(id))
                .findFirst()
                .orElse(null);
    }
}
