package es.onebox.mgmt.events.dto.ticketcontents;

import java.util.ArrayList;
import java.util.Collection;

public class EventTicketContentsTextPassbookListDTO extends ArrayList<EventTicketContentTextPassbookDTO> {

    private static final long serialVersionUID = 1L;

    public EventTicketContentsTextPassbookListDTO() {
    }

    public EventTicketContentsTextPassbookListDTO(Collection<? extends EventTicketContentTextPassbookDTO> c) {
        super(c);
    }
}
