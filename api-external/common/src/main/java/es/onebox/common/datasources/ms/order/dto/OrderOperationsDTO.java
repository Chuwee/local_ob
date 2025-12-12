package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class OrderOperationsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4639424352217318112L;
    private Boolean isRelocated;
    private List<OrderProductAction> actions;

    public Boolean getRelocated() {
        return isRelocated;
    }

    public void setRelocated(Boolean relocated) {
        isRelocated = relocated;
    }

    public List<OrderProductAction> getActions() {
        return actions;
    }

    public void setActions(List<OrderProductAction> actions) {
        this.actions = actions;
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
