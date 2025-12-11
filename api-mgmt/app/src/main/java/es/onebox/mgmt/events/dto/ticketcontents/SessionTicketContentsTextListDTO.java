package es.onebox.mgmt.events.dto.ticketcontents;

import java.util.ArrayList;
import java.util.Collection;

public class SessionTicketContentsTextListDTO extends ArrayList<SessionTicketContentTextPDFDTO> {

    private static final long serialVersionUID = 1L;

    public SessionTicketContentsTextListDTO() {
    }

    public SessionTicketContentsTextListDTO(Collection<? extends SessionTicketContentTextPDFDTO> c) {
        super(c);
    }
}
