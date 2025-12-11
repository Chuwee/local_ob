package es.onebox.mgmt.accesscontrol.dto;

import es.onebox.core.serializer.dto.common.NameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;

public class AccessControlSystemsDTO extends ArrayList<NameDTO> implements Serializable {

    private static final long serialVersionUID = 1980576297346138707L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
