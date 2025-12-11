package es.onebox.mgmt.channels.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;

public class MemberCapacitiesRequestDTO extends ArrayList<MemberCapacityRequestDTO> {

    @Serial
    private static final long serialVersionUID = 6183132751557116420L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}