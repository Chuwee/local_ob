package es.onebox.mgmt.datasources.ms.event.dto.products;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProductCommunicationElementImageListDTO<T extends Serializable> extends ArrayList<ProductCommunicationElementImageDTO<T>> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductCommunicationElementImageListDTO() {
    }

    public ProductCommunicationElementImageListDTO(Collection<? extends ProductCommunicationElementImageDTO<T>> c) {
        super(c);
    }

    public List<ProductCommunicationElementImageDTO<T>> getImages() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
