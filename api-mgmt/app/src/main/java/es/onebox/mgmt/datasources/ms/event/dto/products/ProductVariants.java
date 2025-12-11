package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class ProductVariants extends BaseResponseCollection<ProductVariant, Metadata> {
    @Serial
    private static final long serialVersionUID = 1L;

    public ProductVariants() {
    }

    public ProductVariants(List<ProductVariant> data, Metadata metadata) {
        super(data, metadata);
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

