package es.onebox.mgmt.packs.dto.ticketcontents;

import jakarta.validation.constraints.NotEmpty;

import java.io.Serial;
import java.util.ArrayList;

@NotEmpty
public class PackTicketContentsImagePDFListDTO extends ArrayList<PackTicketContentImagePDFDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    public PackTicketContentsImagePDFListDTO() {
        super();
    }
}
