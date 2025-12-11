package es.onebox.mgmt.externalevents.converter;

import es.onebox.mgmt.datasources.ms.event.dto.externalevent.ExternalEvent;
import es.onebox.mgmt.datasources.ms.event.dto.externalevent.ExternalEventType;
import es.onebox.mgmt.externalevents.dto.ExternalEventDTO;
import es.onebox.mgmt.externalevents.dto.ExternalEventTypeDTO;

public class ExternalEventsConverter {

    private ExternalEventsConverter() {}

    public static ExternalEventType convertExternalEventTypeDTO(ExternalEventTypeDTO eventTypeDTO) {
        if(eventTypeDTO != null) {
            return ExternalEventType.getExternalEventType(eventTypeDTO.name());
        }
        return null;
    }

    public static ExternalEventTypeDTO convertExternalEventType(ExternalEventType eventType) {
        if(eventType != null) {
            return ExternalEventTypeDTO.getExternalEventTypeDTO(eventType.name());
        }
        return null;
    }

    public static ExternalEventDTO convertExternalEvent(ExternalEvent externalEvent) {
        if(externalEvent == null) {
            return null;
        }
        ExternalEventDTO externalEventDTO = new ExternalEventDTO();
        externalEventDTO.setInternalId(externalEvent.getInternalId());
        externalEventDTO.setEventId(externalEvent.getEventId());
        externalEventDTO.setEntityId(externalEvent.getEntityId());
        externalEventDTO.setEventName(externalEvent.getEventName());
        externalEventDTO.setEventType(convertExternalEventType(externalEvent.getEventType()));
        return externalEventDTO;
    }
}
