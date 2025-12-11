package es.onebox.mgmt.apps.enums;

import java.util.stream.Stream;

public enum AppNames {
    PORTAL("portal"),
    CHANNELS("channels"),
    MEMBERS("members"),
    CUSTOMERS("customers");

    private final String name;

    AppNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AppNames getApp(String name) {
        return Stream.of(values()).filter(v -> v.getName().equals(name)).findFirst().orElse(null);
    }
}
