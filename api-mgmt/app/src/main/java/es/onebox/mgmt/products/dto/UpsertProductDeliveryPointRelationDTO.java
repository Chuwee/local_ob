package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpsertProductDeliveryPointRelationDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "deliveryPointIds can not be null")
    @JsonProperty(value = "delivery_point_ids")
    private List<Long> deliveryPointIds;


    public UpsertProductDeliveryPointRelationDTO() {
    }

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
