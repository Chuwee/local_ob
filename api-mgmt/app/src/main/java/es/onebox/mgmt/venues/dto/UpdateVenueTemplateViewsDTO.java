package es.onebox.mgmt.venues.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class UpdateVenueTemplateViewsDTO extends ArrayList<UpdateVenueTemplateViewBulkDTO> {

    private static final long serialVersionUID = 1L;

    public UpdateVenueTemplateViewsDTO() {
    }

    public UpdateVenueTemplateViewsDTO(Collection<UpdateVenueTemplateViewBulkDTO> data) {
        super(data);
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
