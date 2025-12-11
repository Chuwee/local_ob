package es.onebox.mgmt.sessions.dto;

import java.util.ArrayList;
import java.util.Collection;

public class SessionTicketContentsTextPassbookListDTO extends ArrayList<SessionTicketContentTextPassbookDTO> {

    private static final long serialVersionUID = 1L;

    public SessionTicketContentsTextPassbookListDTO() {
    }

    public SessionTicketContentsTextPassbookListDTO(Collection<? extends SessionTicketContentTextPassbookDTO> c) {
        super(c);
    }

}
