package es.onebox.mgmt.events.dto.ticketcontents;

import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.Collection;

@NotEmpty
public class EventTicketContentsTextPDFListDTO extends ArrayList<EventTicketContentTextPDFDTO> {

    private static final long serialVersionUID = 1L;
    
    public EventTicketContentsTextPDFListDTO() {
    }
    
    public EventTicketContentsTextPDFListDTO(Collection<? extends EventTicketContentTextPDFDTO> c) {
        super(c);
    }
}
