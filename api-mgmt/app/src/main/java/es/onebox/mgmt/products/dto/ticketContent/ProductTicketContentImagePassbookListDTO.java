package es.onebox.mgmt.products.dto.ticketContent;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

public class ProductTicketContentImagePassbookListDTO extends ArrayList<ProductTicketContentImagePassbookDTO> {
    @Serial
    private static final long serialVersionUID = 1L;

    public ProductTicketContentImagePassbookListDTO() {
        super();
    }

    public ProductTicketContentImagePassbookListDTO(Collection<? extends ProductTicketContentImagePassbookDTO> c) {
        super(c);
    }
}

