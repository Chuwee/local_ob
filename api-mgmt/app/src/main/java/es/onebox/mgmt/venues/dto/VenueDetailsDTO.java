package es.onebox.mgmt.venues.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class VenueDetailsDTO extends VenueItemDTO {
    @Serial private static final long serialVersionUID = 1L;

    private List<IdNameDTO> spaces;

    public List<IdNameDTO> getSpaces() {
        return spaces;
    }
    public void setSpaces(List<IdNameDTO> spaces) {
        this.spaces = spaces;
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
