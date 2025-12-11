package es.onebox.event.catalog.elasticsearch.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum VirtualQueueVersion {
    V3("v3");

    private final String sessionConfigValue;

    VirtualQueueVersion(String sessionConfigValue) {
        this.sessionConfigValue = sessionConfigValue;
    }

    public String getSessionConfigValue() {
        return sessionConfigValue;
    }

    public static VirtualQueueVersion getFromConfigValue(String value) {
        if (StringUtils.isBlank(value)) {
            return V3;
        }
        return Arrays.stream(VirtualQueueVersion.values()).filter(v -> value.equals(v.sessionConfigValue)).findFirst().orElse(null);
    }
}
