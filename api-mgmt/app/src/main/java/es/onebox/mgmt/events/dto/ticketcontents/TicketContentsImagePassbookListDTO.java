package es.onebox.mgmt.events.dto.ticketcontents;

import java.util.ArrayList;
import java.util.Collection;

public class TicketContentsImagePassbookListDTO extends ArrayList<TicketContentImagePassbookDTO> {

    private static final long serialVersionUID = 1L;

    public TicketContentsImagePassbookListDTO() {
        super();
    }

    public TicketContentsImagePassbookListDTO(Collection<? extends TicketContentImagePassbookDTO> c) {
        super(c);
    }
}
