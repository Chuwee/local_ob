package es.onebox.common.datasources.distribution.dto.deliverymethods;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.distribution.dto.order.OrderType;

import java.io.Serial;
import java.io.Serializable;

public class PreConfirmRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 66753351696370144L;

    @JsonProperty("type")
    private OrderType type;
    @JsonProperty("delivery_method")
    private OrderDeliveryMethod deliveryMethod;
    @JsonProperty("delivery_method_cost")
    private Double cost;

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public OrderDeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(OrderDeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}

