package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class Products extends ListWithMetadata<Product> {

    @Serial
    private static final long serialVersionUID = 6016217542772954098L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
