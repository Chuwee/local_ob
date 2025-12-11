package es.onebox.event.products.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;

public class ProductTicketContentListTextDTO extends ArrayList<ProductTicketContentTextDTO> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
