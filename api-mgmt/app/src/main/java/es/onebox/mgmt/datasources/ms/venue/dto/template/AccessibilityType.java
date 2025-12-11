package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serializable;
import java.util.stream.Stream;

public enum AccessibilityType implements Serializable {

    NORMAL(1),
    REDUCED_MOBILITY(2);

    private int status;

    public int getStatus() {
        return status;
    }

    AccessibilityType(int status) {
        this.status = status;
    }

    public static AccessibilityType byId(Integer id) {
        return Stream.of(AccessibilityType.values())
                .filter(v -> v.getStatus() == id)
                .findFirst()
                .orElse(null);
    }

}
