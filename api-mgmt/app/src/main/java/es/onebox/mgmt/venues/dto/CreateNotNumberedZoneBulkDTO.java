package es.onebox.mgmt.venues.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.HashSet;

public class CreateNotNumberedZoneBulkDTO extends HashSet<CreateNotNumberedZoneDTO> {

    private static final long serialVersionUID = 1L;

    public CreateNotNumberedZoneBulkDTO() {
    }

    public CreateNotNumberedZoneBulkDTO(Collection<? extends CreateNotNumberedZoneDTO> c) {
        super(c);
    }

    public CreateNotNumberedZoneBulkDTO(int initialCapacity) {
        super(initialCapacity);
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
