package es.onebox.mgmt.packs.dto.ticketcontents;

import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;

@NotEmpty
public class PackTicketContentsTextListDTO extends ArrayList<PackTicketContentTextDTO> {

    private static final long serialVersionUID = 1L;

    public PackTicketContentsTextListDTO() {
    }
}
