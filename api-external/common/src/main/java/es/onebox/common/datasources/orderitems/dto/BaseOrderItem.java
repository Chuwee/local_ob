package es.onebox.common.datasources.orderitems.dto;

import es.onebox.common.datasources.orderitems.enums.OrderItemType;
import es.onebox.common.datasources.orders.dto.ItemPrice;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BaseOrderItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private OrderItemType type;
    private ItemPrice price;
    private transient Map<String, Object> attendant = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderItemType getType() {
        return type;
    }

    public void setType(OrderItemType type) {
        this.type = type;
    }

    public ItemPrice getPrice() {
        return price;
    }

    public void setPrice(ItemPrice price) {
        this.price = price;
    }

    public Map<String, Object> getAttendant() {
        return attendant;
    }

    public void setAttendant(Map<String, Object> attendant) {
        this.attendant = attendant;
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
