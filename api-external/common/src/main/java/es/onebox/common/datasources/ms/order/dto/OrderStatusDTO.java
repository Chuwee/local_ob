package es.onebox.common.datasources.ms.order.dto;

import es.onebox.common.datasources.ms.order.enums.OrderState;
import es.onebox.common.datasources.ms.order.enums.OrderType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class OrderStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3903407655575216878L;

    private OrderType type;
    private OrderState state;

    public void setType(OrderType type) {
        this.type = type;
    }

    public OrderType getType() {
        return type;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
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
