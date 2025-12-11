package es.onebox.mgmt.datasources.ms.channel.enums;

import java.util.stream.Stream;

public enum ChannelScope {

    LOCAL(0),
    GLOBAL(1);

    private int scope;

    ChannelScope(int scope) {
        this.scope = scope;
    }

    public static ChannelScope get(int scope) {
        return Stream.of(values())
                .filter(cs -> cs.scope == scope)
                .findAny()
                .orElse(null);
    }

    public int getScope() {
        return scope;
    }
}
