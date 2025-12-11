package es.onebox.event.producttickettemplate.domain.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class ProductTicketTemplateLiteralsDTO extends ArrayList<ProductTicketTemplateLiteralDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 4215062652352662125L;


    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
