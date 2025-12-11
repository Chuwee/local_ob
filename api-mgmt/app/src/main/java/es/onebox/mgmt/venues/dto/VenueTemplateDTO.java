package es.onebox.mgmt.venues.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VenueTemplateDTO extends BaseVenueTemplateDTO {

    private static final long serialVersionUID = 1L;

    private BaseVenueDTO venue;

    public BaseVenueDTO getVenue() {
        return venue;
    }

    public void setVenue(BaseVenueDTO venue) {
        this.venue = venue;
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
