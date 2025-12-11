package es.onebox.mgmt.datasources.ms.entity.enums;

import java.util.Arrays;

public enum EntityVisibilityType {
    PRIVATE("PRIVATE"),
    PUBLIC("PUBLIC"),
    CUSTOM("FILTERED");

    private String publicValue;

    EntityVisibilityType(String publicValue) {
        this.publicValue = publicValue;
    }

    public String getPublicValue() {
        return publicValue;
    }

    public static EntityVisibilityType fromPublicValue(String v) {
        if (v == null) return null;
        return Arrays.stream(EntityVisibilityType.values()).filter(t -> t.getPublicValue().equals(v)).findFirst().orElse(null);
    }
}
