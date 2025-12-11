package es.onebox.mgmt.pricetype.dto;

import java.util.ArrayList;
import java.util.Collection;

public class PriceTypeTicketContentsImagePrinterListDTO extends ArrayList<PriceTypeTicketContentImagePrinterDTO> {

    private static final long serialVersionUID = 1L;

    public PriceTypeTicketContentsImagePrinterListDTO() {
        super();
    }

    public PriceTypeTicketContentsImagePrinterListDTO(Collection<? extends PriceTypeTicketContentImagePrinterDTO> c) {
        super(c);
    }
}
