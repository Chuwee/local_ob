package es.onebox.event.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductEventDeliveryPointDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private IdNameDTO product;
    private IdNameDTO event;
    private IdNameDTO deliveryPoint;
    @JsonProperty("isDefault")
    private boolean isDefault;

    public IdNameDTO getProduct() {
        return product;
    }

    public void setProduct(IdNameDTO product) {
        this.product = product;
    }

    public IdNameDTO getEvent() {
        return event;
    }

    public void setEvent(IdNameDTO event) {
        this.event = event;
    }

    public IdNameDTO getDeliveryPoint() {
        return deliveryPoint;
    }

    public void setDeliveryPoint(IdNameDTO deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
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
