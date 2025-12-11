package es.onebox.mgmt.datasources.ms.event.dto.externalevent;

import java.util.Arrays;

public enum ExternalEventType {
    EVENT,
    SEASON_TICKET;

    public static ExternalEventType getExternalEventType(String type) {
        return Arrays.stream(ExternalEventType.values())
                .filter(eventType -> eventType.name().equals(type))
                .findFirst()
                .orElse(EVENT);
    }
}
