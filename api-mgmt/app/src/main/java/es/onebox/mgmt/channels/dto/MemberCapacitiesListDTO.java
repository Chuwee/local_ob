package es.onebox.mgmt.channels.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class MemberCapacitiesListDTO extends ArrayList<MemberCapacityDTO> {

    @Serial
    private static final long serialVersionUID = 6333240498004125369L;

    public MemberCapacitiesListDTO(List<MemberCapacityDTO> memberCapacitiesList) {
        this.addAll(memberCapacitiesList);
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