package es.onebox.mgmt.venues.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.HashSet;

public class UpdateNotNumberedZonesBulkDTO extends HashSet<UpdateNotNumberedZoneBulkDTO> {

    private static final long serialVersionUID = 1L;


    public UpdateNotNumberedZonesBulkDTO() {
    }

    public UpdateNotNumberedZonesBulkDTO(Collection<? extends UpdateNotNumberedZoneBulkDTO> c) {
        super(c);
    }

    public UpdateNotNumberedZonesBulkDTO(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public UpdateNotNumberedZonesBulkDTO(int initialCapacity) {
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
