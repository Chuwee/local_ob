package es.onebox.mgmt.products.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductCommunicationElementsTextsDTO extends java.util.ArrayList<ProductCommunicationElementTextDTO> implements Serializable {
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
