package es.onebox.mgmt.salerequests.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class VenueSaleRequestDTO extends BaseVenueSaleRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocationSaleRequestDTO location;

    public LocationSaleRequestDTO getLocation() {
        return location;
    }

    public void setLocation(LocationSaleRequestDTO location) {
        this.location = location;
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
