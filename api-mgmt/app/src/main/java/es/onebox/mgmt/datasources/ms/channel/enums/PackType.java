package es.onebox.mgmt.datasources.ms.channel.enums;

import java.util.Arrays;

public enum PackType {
    MANUAL,
    AUTOMATIC;

    public static PackType getByName(String name) {
        return Arrays.stream(PackType.values())
                .filter(value -> value.name().equals(name))
                .findFirst()
                .orElse(null);
    }

}
