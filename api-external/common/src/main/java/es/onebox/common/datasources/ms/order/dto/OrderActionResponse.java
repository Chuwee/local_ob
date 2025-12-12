package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderActionResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String orderCode;
    private List<OrderProductActionDTO> productActions = new ArrayList<>();

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public List<OrderProductActionDTO> getProductActions() {
        return productActions;
    }

    public void setProductActions(List<OrderProductActionDTO> productActions) {
        this.productActions = productActions;
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
