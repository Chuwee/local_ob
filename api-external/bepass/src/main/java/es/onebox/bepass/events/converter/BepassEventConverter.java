package es.onebox.bepass.events.converter;

import es.onebox.bepass.auth.BepassAuthContext;
import es.onebox.bepass.datasources.bepass.dto.Event;
import es.onebox.bepass.events.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.bepass.datasources.bepass.dto.CreateEventRequest;
import es.onebox.bepass.datasources.bepass.dto.UpdateEventRequest;
import es.onebox.bepass.events.dto.UpdateEventDTO;

import java.time.LocalDateTime;

public class BepassEventConverter {

    private BepassEventConverter() {
    }

    public static CreateEventRequest toCreateEvent(SessionDTO in, String locationId) {
        CreateEventRequest out = new CreateEventRequest();
        out.setExternalId(String.valueOf(in.getId()));
        out.setLocationId(locationId);
        out.setEventName(in.getName());
        out.setStartDateTime(in.getDate().getStart());
        out.setEndDateTime(in.getDate().getEnd());
        out.setActive(Boolean.TRUE);
        out.setCompanyId(BepassAuthContext.get().companyId());
        return out;
    }

    public static UpdateEventRequest toUpdateEvent(UpdateEventDTO body) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (body.start() != null) {
            start = body.start().toLocalDateTime();
        }
        if (body.end() != null) {
            end = body.end().toLocalDateTime();
        }
        return new UpdateEventRequest(body.active(), body.locationId(), body.eventName(), start, end);
    }

    public static EventDTO toEventDTO(Event event) {
        return new EventDTO(event.getId(), event.getExternalId(),
                event.getEventName(), event.getStartDateTime(), event.getEndDateTime(), event.getStatus());
    }
}
