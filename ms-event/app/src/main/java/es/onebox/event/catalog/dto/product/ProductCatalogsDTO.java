package es.onebox.event.catalog.dto.product;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;

public class ProductCatalogsDTO extends ArrayList<ProductCatalogDTO>  {

    @Serial
    private static final long serialVersionUID = -2581626070260784633L;


    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
