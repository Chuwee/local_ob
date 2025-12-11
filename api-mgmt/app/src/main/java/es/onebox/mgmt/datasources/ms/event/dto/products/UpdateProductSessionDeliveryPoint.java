package es.onebox.mgmt.datasources.ms.event.dto.products;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateProductSessionDeliveryPoint implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private List<UpdateProductSessionDeliveryPointDetail> deliveryPoints;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<UpdateProductSessionDeliveryPointDetail> getDeliveryPoints() {
        return deliveryPoints;
    }

    public void setDeliveryPoints(List<UpdateProductSessionDeliveryPointDetail> deliveryPoints) {
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
