package es.onebox.common.datasources.orderitems.dto;

import es.onebox.common.datasources.common.dto.Metadata;
import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class SearchOrderItemsResponse extends BaseResponseCollection<OrderItem, Metadata> {

    @Serial
    private static final long serialVersionUID = -680724336619155081L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
