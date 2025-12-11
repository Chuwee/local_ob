package es.onebox.mgmt.events.dto.ticketcontents;

import java.util.ArrayList;
import java.util.Collection;

public class SessionTicketContentsImagePrinterListDTO extends ArrayList<SessionTicketContentImagePrinterDTO> {

    private static final long serialVersionUID = 1L;

    public SessionTicketContentsImagePrinterListDTO() {
        super();
    }

    public SessionTicketContentsImagePrinterListDTO(Collection<? extends SessionTicketContentImagePrinterDTO> c) {
        super(c);
    }
}
