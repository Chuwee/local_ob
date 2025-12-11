package es.onebox.event.products.dto;

import es.onebox.event.products.enums.DeliveryPointStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;

public class UpdateDeliveryPointDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Length(max = 50, message = "name max size is 50")
    @Pattern(regexp = "^[^|]*$", message = "Invalid characters. | is not allowed in the name")
    private String name;

    private UpdateDeliveryPointAddressDTO location;

    private DeliveryPointStatus status;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UpdateDeliveryPointAddressDTO getLocation() {
        return location;
    }

    public void setLocation(UpdateDeliveryPointAddressDTO location) {
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

