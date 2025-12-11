package es.onebox.mgmt.events.dto.ticketcontents;

import java.util.ArrayList;
import java.util.Collection;

public class EventTicketContentsImagePrinterListDTO extends ArrayList<EventTicketContentImagePrinterDTO> {

    private static final long serialVersionUID = 1L;

    public EventTicketContentsImagePrinterListDTO() {
        super();
    }

    public EventTicketContentsImagePrinterListDTO(Collection<? extends EventTicketContentImagePrinterDTO> c) {
        super(c);
    }
}
