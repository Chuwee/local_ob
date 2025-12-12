package es.onebox.common.datasources.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.common.dto.OperativeGrant;
import es.onebox.common.datasources.orderitems.dto.BaseOrderItem;
import es.onebox.common.datasources.orders.enums.OrderDetailsItemState;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;

public class BaseOrderItemDetail extends BaseOrderItem {


    @Serial
    private static final long serialVersionUID = 1L;

    private OrderDetailsItemState state;
    @JsonProperty("previous_order")
    private OrderRelated previousOrder;
    @JsonProperty("next_order")
    private OrderRelated nextOrder;
    private OperativeGrant action;

    public OperativeGrant getAction() {
        return action;
    }

    public void setAction(OperativeGrant action) {
        this.action = action;
    }

    public OrderDetailsItemState getState() {
        return state;
    }

    public void setState(OrderDetailsItemState state) {
        this.state = state;
    }

    public OrderRelated getPreviousOrder() {
        return previousOrder;
    }

    public void setPreviousOrder(OrderRelated previousOrder) {
        this.previousOrder = previousOrder;
    }

    public OrderRelated getNextOrder() {
        return nextOrder;
    }

    public void setNextOrder(OrderRelated nextOrder) {
        this.nextOrder = nextOrder;
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
        return ToStringBuilder.reflectionToString(this);
    }
}
