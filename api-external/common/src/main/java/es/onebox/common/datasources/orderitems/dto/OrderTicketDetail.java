package es.onebox.common.datasources.orderitems.dto;

import es.onebox.common.datasources.orders.dto.BaseTicketDetail;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class OrderTicketDetail extends BaseTicketDetail {

    @Serial
    private static final long serialVersionUID = 1L;

    private OrderTicketDetailAllocation allocation;

    public OrderTicketDetailAllocation getAllocation() {
        return allocation;
    }

    public void setAllocation(OrderTicketDetailAllocation allocation) {
        this.allocation = allocation;
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
