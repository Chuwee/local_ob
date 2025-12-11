package es.onebox.mgmt.datasources.ms.order.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.dal.dto.couch.order.OrderDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class OrderSearchResponse extends ListWithMetadata<OrderDTO> {

    private static final long serialVersionUID = 1L;

    public OrderSearchResponse() {
    }

    public OrderSearchResponse(List<OrderDTO> data) {
        super.setData(data);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
