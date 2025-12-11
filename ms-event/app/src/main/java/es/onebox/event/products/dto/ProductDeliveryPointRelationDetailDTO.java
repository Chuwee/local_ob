package es.onebox.event.products.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductDeliveryPointRelationDetailDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private IdNameDTO deliveryPoint;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IdNameDTO getDeliveryPoint() {
        return deliveryPoint;
    }

    public void setDeliveryPoint(IdNameDTO deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
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

