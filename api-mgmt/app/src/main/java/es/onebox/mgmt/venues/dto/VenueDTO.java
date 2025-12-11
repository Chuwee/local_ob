package es.onebox.mgmt.venues.dto;

import es.onebox.mgmt.venues.enums.VenueStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class VenueDTO extends BaseVenueDTO {
    @Serial private static final long serialVersionUID = 1L;

    private Integer capacity;
    private VenueStatus status;

    public Integer getCapacity() {
        return capacity;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public VenueStatus getStatus() {
        return status;
    }
    public void setStatus(VenueStatus status) {
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
