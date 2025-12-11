package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateProductSessionDeliveryPointDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long id;

    @JsonProperty(value = "delivery_points")
    private List<UpdateProductSessionDeliveryPointDetailDTO> deliveryPoints;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<UpdateProductSessionDeliveryPointDetailDTO> getDeliveryPoints() {
        return deliveryPoints;
    }

    public void setDeliveryPoints(List<UpdateProductSessionDeliveryPointDetailDTO> deliveryPoints) {
        this.deliveryPoints = deliveryPoints;
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
