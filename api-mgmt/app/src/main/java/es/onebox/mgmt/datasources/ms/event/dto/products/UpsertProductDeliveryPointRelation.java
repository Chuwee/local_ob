package es.onebox.mgmt.datasources.ms.event.dto.products;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpsertProductDeliveryPointRelation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<Long> deliveryPointIds;


    public UpsertProductDeliveryPointRelation() {
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
