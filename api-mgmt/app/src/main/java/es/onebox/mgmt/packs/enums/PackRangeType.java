package es.onebox.mgmt.packs.enums;

import java.util.Arrays;

public enum PackRangeType {
    AUTOMATIC,
    CUSTOM;

    public static PackRangeType getByName(String name) {
        return Arrays.stream(PackRangeType.values())
                .filter(value -> value.name().equals(name))
                .findFirst()
                .orElse(null);
    }

}
