package es.onebox.mgmt.channels.enums;

import java.util.stream.Stream;

public enum ChannelPortalBuild {
    MAIN("main"),
    MMC("mmc"),
    STH("sth"),
    WIDGET("widget"),
    BOXOFFICE("boxoffice"),
    CLIENTS("clients");

    private String build;

    ChannelPortalBuild(String build) {
        this.build = build;
    }

    public String getBuild() {
        return this.build;
    }

    public static ChannelPortalBuild getByBuild(String build) {
        if(build == null) {
            return null;
        }
        return Stream.of(values())
                .filter(status -> status.build.equals(build))
                .findFirst()
                .orElse(null);
    }
}
