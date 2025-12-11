package es.onebox.mgmt.datasources.ms.channel.enums;

import java.util.stream.Stream;

public enum ChannelBuild {
    MAIN("main"),
    MMC("mmc"),
    STH("sth"),
    WIDGET("widget"),
    BOXOFFICE("boxoffice"),
    CLIENTS("clients");

    private String build;

    ChannelBuild(String build) {
        this.build = build;
    }

    public String getBuild() {
        return this.build;
    }

    public static ChannelBuild getByBuild(String build) {
        if (build == null) {
            return null;
        }
        return Stream.of(values())
                .filter(status -> status.build.equals(build))
                .findFirst()
                .orElse(null);
    }

    public static ChannelBuild fromValue(String value) {
        return valueOf(value);
    }
}
