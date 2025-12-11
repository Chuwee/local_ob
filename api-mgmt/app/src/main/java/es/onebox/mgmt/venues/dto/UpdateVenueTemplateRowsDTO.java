package es.onebox.mgmt.venues.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class UpdateVenueTemplateRowsDTO extends ArrayList<UpdateVenueTemplateRowBulkDTO> {

    private static final long serialVersionUID = 1L;

    public UpdateVenueTemplateRowsDTO() {
    }

    public UpdateVenueTemplateRowsDTO(Collection<UpdateVenueTemplateRowBulkDTO> data) {
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
