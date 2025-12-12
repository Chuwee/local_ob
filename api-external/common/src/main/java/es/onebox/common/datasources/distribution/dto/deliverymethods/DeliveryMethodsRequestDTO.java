package es.onebox.common.datasources.distribution.dto.deliverymethods;

import java.io.Serial;
import java.io.Serializable;

public class DeliveryMethodsRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -2092545342042880344L;
    private OrderDeliveryMethod type;
    private Double cost;

    public OrderDeliveryMethod getType() {
        return type;
    }

    public void setType(OrderDeliveryMethod type) {
        this.type = type;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
