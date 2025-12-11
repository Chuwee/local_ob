package es.onebox.mgmt.datasources.ms.order.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.dal.dto.couch.order.OrderProductDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ProductSearchResponse extends ListWithMetadata<OrderProductDTO> {

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
