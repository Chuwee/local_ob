package es.onebox.mgmt.pricetype.dto;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

public class PriceTypeTicketContentsImagePDFListDTO extends ArrayList<PriceTypeTicketContentImagePDFDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    public PriceTypeTicketContentsImagePDFListDTO() {
        super();
    }

    public PriceTypeTicketContentsImagePDFListDTO(Collection<? extends PriceTypeTicketContentImagePDFDTO> c) {
        super(c);
    }
}
