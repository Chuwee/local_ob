package es.onebox.mgmt.products.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProductContentTextListDTO<T extends Serializable> extends ArrayList<ProductContentTextDTO<T>> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductContentTextListDTO() {
    }

    public ProductContentTextListDTO(Collection<? extends ProductContentTextDTO<T>> c) {
        super(c);
    }

    public List<ProductContentTextDTO<T>> getTexts() {
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
