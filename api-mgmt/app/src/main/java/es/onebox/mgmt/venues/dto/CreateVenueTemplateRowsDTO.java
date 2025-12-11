package es.onebox.mgmt.venues.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class CreateVenueTemplateRowsDTO extends ArrayList<CreateVenueTemplateRowDTO> {

    private static final long serialVersionUID = 1L;

    public CreateVenueTemplateRowsDTO() {
    }

    public CreateVenueTemplateRowsDTO(Collection<CreateVenueTemplateRowDTO> data) {
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
