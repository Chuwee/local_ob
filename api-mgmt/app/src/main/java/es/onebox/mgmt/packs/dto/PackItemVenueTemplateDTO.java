package es.onebox.mgmt.packs.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PackItemVenueTemplateDTO extends IdNameDTO implements Serializable {

    private static final long serialVersionUID = 5553824240649464486L;

    private PackItemVenueDTO venue;

    public PackItemVenueDTO getVenue() {
        return venue;
    }

    public void setVenue(PackItemVenueDTO venue) {
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
