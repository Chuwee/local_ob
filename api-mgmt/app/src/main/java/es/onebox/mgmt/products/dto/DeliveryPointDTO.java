package es.onebox.mgmt.products.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.products.enums.DeliveryPointStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class DeliveryPointDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private IdNameDTO entity;
    private String name;
    private DeliveryPointAddressDTO location;
    private DeliveryPointStatus status;

    public DeliveryPointDTO() {
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

    public DeliveryPointAddressDTO getLocation() {
        return location;
    }

    public void setLocation(DeliveryPointAddressDTO location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
