package es.onebox.mgmt.externalevents.dto;

import java.util.Arrays;

public enum ExternalEventTypeDTO {
    EVENT,
    SEASON_TICKET;

    public static ExternalEventTypeDTO getExternalEventTypeDTO(String type) {
        return Arrays.stream(ExternalEventTypeDTO.values())
                .filter(eventType -> eventType.name().equals(type))
                .findFirst()
                .orElse(EVENT);
    }
}
