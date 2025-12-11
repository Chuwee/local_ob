package es.onebox.mgmt.events.dto.ticketcontents;

import java.util.ArrayList;
import java.util.Collection;

public class SessionTicketContentsImagePDFListDTO extends ArrayList<SessionTicketContentImagePDFDTO> {

    private static final long serialVersionUID = 1L;

    public SessionTicketContentsImagePDFListDTO() {
        super();
    }

    public SessionTicketContentsImagePDFListDTO(Collection<? extends SessionTicketContentImagePDFDTO> c) {
        super(c);
    }
}
