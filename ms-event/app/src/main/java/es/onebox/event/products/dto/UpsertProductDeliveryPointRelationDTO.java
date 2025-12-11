package es.onebox.event.products.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpsertProductDeliveryPointRelationDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "deliveryPointIds can not be null")
    private List<Long> deliveryPointIds;

    public List<Long> getDeliveryPointIds() {
        return deliveryPointIds;
    }

    public void setDeliveryPointIds(List<Long> deliveryPointIds) {
        this.deliveryPointIds = deliveryPointIds;
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

