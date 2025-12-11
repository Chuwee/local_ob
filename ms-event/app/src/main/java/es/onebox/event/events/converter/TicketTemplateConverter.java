package es.onebox.event.events.converter;

import es.onebox.event.events.dto.TicketTemplateDTO;
import es.onebox.event.events.enums.TicketFormat;
import es.onebox.event.tickettemplates.dao.TicketTemplateRecord;

public class TicketTemplateConverter {

    private TicketTemplateConverter() {
    }

    public static TicketTemplateDTO convert(TicketTemplateRecord ticketTemplateRecord) {
        if (ticketTemplateRecord == null) {
            return null;
        }
        TicketTemplateDTO ticketTemplateDTO = new TicketTemplateDTO();
        ticketTemplateDTO.setId(ticketTemplateRecord.getIdplantilla().longValue());
        ticketTemplateDTO.setName(ticketTemplateRecord.getNombre());
        ticketTemplateDTO.setTicketFormat(TicketFormat.byId(ticketTemplateRecord.getModelFormat()));
        return ticketTemplateDTO;
    }

}
