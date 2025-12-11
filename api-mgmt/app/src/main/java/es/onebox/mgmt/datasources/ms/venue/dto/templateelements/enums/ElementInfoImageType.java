package es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums;

import java.util.Arrays;

public enum ElementInfoImageType {

    SLIDER,
    HIGHLIGHTED;

    public static ElementInfoImageType getByName(String name) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

}
