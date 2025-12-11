package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.util.Arrays;

public enum SessionVirtualQueueVersion {
    V3("v3");

    private final String name;

    SessionVirtualQueueVersion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SessionVirtualQueueVersion getByName(String version) {
        if (version == null) {
            return V3;
        } else {
            return Arrays.stream(values())
                    .filter(v -> v.getName().equals(version))
                    .findAny()
                    .orElse(V3);
        }
    }
}
