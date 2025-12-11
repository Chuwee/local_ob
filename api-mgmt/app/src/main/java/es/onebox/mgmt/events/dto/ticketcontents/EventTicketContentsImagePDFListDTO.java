package es.onebox.mgmt.events.dto.ticketcontents;

import jakarta.validation.constraints.NotEmpty;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

@NotEmpty
public class EventTicketContentsImagePDFListDTO extends ArrayList<EventTicketContentImagePDFDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    public EventTicketContentsImagePDFListDTO() {
        super();
    }

    public EventTicketContentsImagePDFListDTO(Collection<? extends EventTicketContentImagePDFDTO> c) {
        super(c);
    }
}
