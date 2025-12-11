package es.onebox.mgmt.common.ticketpreview.converter;

import es.onebox.mgmt.common.ticketpreview.TicketPreviewDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPreview;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.enums.TicketPreviewType;
import es.onebox.mgmt.events.enums.EventTicketTemplateType;

public class TicketPreviewConverter {

    private TicketPreviewConverter() {}

    public static TicketPreviewDTO toDTO(TicketPreview source) {
        TicketPreviewDTO target = new TicketPreviewDTO();
        target.setUrl(source.getUrl());
        return target;
    }

    public static TicketPreviewRequest toRequest(Long eventId, EventTicketTemplateType ticketType, Event event, Long languageId) {
        TicketPreviewRequest request = new TicketPreviewRequest();
        request.setEntityId(event.getEntityId());
        request.setEventId(eventId);
        request.setLanguageId(languageId);
        request.setItemId(eventId);
        request.setType(TicketPreviewType.valueOf(ticketType.name()));
        return request;
    }

}
