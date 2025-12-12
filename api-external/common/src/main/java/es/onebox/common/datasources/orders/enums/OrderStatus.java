package es.onebox.common.datasources.orders.enums;

import es.onebox.common.datasources.common.enums.OrderType;

import java.io.Serializable;

public class OrderStatus implements Serializable{

    private static final long serialVersionUID = 7523039817755860336L;

    OrderType type;
    OrderState state;

    public OrderStatus() {
    }

    public OrderStatus(OrderStatus status) {
        this(status.getType(), status.getState());
    }

    public OrderStatus(OrderType type, OrderState state) {
        this.type = type;
        this.state = state;
    }

    public void setOrderStatus(OrderStatus status) {
        this.type = status.getType();
        this.state = status.getState();
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrderStatus that = (OrderStatus) o;

        if (type != that.type) {
            return false;
        }
        return state == that.state;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "[" + type + "-" + state + ']';
    }

}
