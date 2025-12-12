package es.onebox.common.datasources.ms.entity.dto;

import es.onebox.common.datasources.ms.entity.enums.InteractiveVenueType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EntityInteractiveVenue implements Serializable {

    @Serial
    private static final long serialVersionUID = 950615522026754887L;
    private Boolean enabled;
    private List<InteractiveVenueType> allowedVenues;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<InteractiveVenueType> getAllowedVenues() {
        return allowedVenues;
    }

    public void setAllowedVenues(List<InteractiveVenueType> allowedVenues) {
        this.allowedVenues = allowedVenues;
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
