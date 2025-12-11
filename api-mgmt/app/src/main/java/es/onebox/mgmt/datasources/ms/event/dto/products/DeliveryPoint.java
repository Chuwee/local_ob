package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.products.enums.DeliveryPointStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class DeliveryPoint implements Serializable {

    @Serial
    private static final long serialVersionUID = 6016217542772954098L;

    private Long id;
    private IdNameDTO entity;
    private String name;
    private DeliveryPointAddress location;
    private DeliveryPointStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeliveryPointAddress getLocation() {
        return location;
    }

    public void setLocation(DeliveryPointAddress location) {
        this.location = location;
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
