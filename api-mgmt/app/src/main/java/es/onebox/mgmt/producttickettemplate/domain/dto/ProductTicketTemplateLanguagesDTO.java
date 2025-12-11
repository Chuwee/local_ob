package es.onebox.mgmt.producttickettemplate.domain.dto;

import es.onebox.mgmt.products.dto.ProductLanguageDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductTicketTemplateLanguagesDTO extends java.util.ArrayList<ProductTicketTemplateLanguageDTO> implements Serializable {
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
