package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.products.enums.ticketContent.ProductTicketContentTextType;

import java.io.Serial;
import java.util.ArrayList;

public class ProductTicketContentTextList extends ArrayList<ProductTicketContentText<ProductTicketContentTextType>> {
    @Serial
    private static final long serialVersionUID = 1L;

    public ProductTicketContentTextList() {
    }
}
