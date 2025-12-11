package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.products.enums.DeliveryPointStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateDeliveryPoint implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private UpdateDeliveryPointAddress location;
    private DeliveryPointStatus status;

    public UpdateDeliveryPoint() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(UpdateDeliveryPointAddress location) {
        this.location = location;
    }

    public UpdateDeliveryPointAddress getLocation() {
        return location;
    }

    public DeliveryPointStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryPointStatus status) {
        this.status = status;
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
