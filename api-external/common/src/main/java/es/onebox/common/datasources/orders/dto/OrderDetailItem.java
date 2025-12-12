package es.onebox.common.datasources.orders.dto;

import es.onebox.common.datasources.orderitems.dto.OrderTicketDetail;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class OrderDetailItem extends BaseOrderItemDetail {

    @Serial
    private static final long serialVersionUID = 6381292319307413497L;

    private OrderTicketDetail ticket;

    public OrderTicketDetail getTicket() {
        return ticket;
    }

    public void setTicket(OrderTicketDetail ticket) {
        this.ticket = ticket;
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
