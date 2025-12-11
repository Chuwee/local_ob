package es.onebox.mgmt.datasources.ms.event.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

public class ProductValueLiterals extends ArrayList<ProductValueLiteral> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductValueLiterals(Collection<ProductValueLiteral> in) {
        super(in);
    }

    public ProductValueLiterals() {
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
