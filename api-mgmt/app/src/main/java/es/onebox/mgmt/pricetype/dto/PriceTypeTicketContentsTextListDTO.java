package es.onebox.mgmt.pricetype.dto;

import java.util.ArrayList;
import java.util.Collection;

public class PriceTypeTicketContentsTextListDTO extends ArrayList<PriceTypeTicketContentTextDTO> {

    private static final long serialVersionUID = 1L;

    public PriceTypeTicketContentsTextListDTO() {
    }

    public PriceTypeTicketContentsTextListDTO(Collection<? extends PriceTypeTicketContentTextDTO> c) {
        super(c);
    }
}
