package es.onebox.mgmt.entities.externalconfiguration.converter;

import es.onebox.mgmt.entities.externalconfiguration.enums.Protocol;

public class ProtocolConverter {

    public static String toMs(Protocol protocol) {
        if (protocol == null) {
            return null;
        }
        return switch (protocol) {
            case HTTP -> "http";
            case HTTPS -> "https";
        };
    }

    public static Protocol toDto(String protocol) {
        if (protocol == null) {
            return null;
        }
        return switch (protocol) {
            case "http" -> Protocol.HTTP;
            case "https" -> Protocol.HTTPS;
            default -> throw new IllegalStateException("Unexpected value: " + protocol);
        };
    }
}
