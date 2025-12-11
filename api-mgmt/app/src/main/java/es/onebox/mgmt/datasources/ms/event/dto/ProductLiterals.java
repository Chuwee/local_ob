package es.onebox.mgmt.datasources.ms.event.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

public class ProductLiterals extends ArrayList<ProductLiteral> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductLiterals(Collection<ProductLiteral> in) {
        super(in);
    }

    public ProductLiterals() {
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
