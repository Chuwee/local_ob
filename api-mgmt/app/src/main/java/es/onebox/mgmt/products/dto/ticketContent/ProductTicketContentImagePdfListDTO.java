package es.onebox.mgmt.products.dto.ticketContent;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

public class ProductTicketContentImagePdfListDTO extends ArrayList<ProductTicketContentImagePdfDTO> {
    @Serial
    private static final long serialVersionUID = 1L;

    public ProductTicketContentImagePdfListDTO() {
        super();
    }

    public ProductTicketContentImagePdfListDTO(Collection<? extends ProductTicketContentImagePdfDTO> c) {
        super(c);
    }
}
