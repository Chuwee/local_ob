package es.onebox.mgmt.producttickettemplate.dto;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

public class ProductTicketTemplateContentLiteralListDTO extends ArrayList<ProductTicketTemplateContentLiteralDTO> {
    @Serial
    private static final long serialVersionUID = 2835085718965348813L;

    public ProductTicketTemplateContentLiteralListDTO() {
    }

    public ProductTicketTemplateContentLiteralListDTO(Collection<? extends ProductTicketTemplateContentLiteralDTO> c) {
        super(c);
    }
}
