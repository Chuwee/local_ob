package es.onebox.mgmt.entities.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

public class CustomManagementsDTO extends ArrayList<CustomManagementDTO> {

    @Serial
    private static final long serialVersionUID = -6168648764579959130L;

    public CustomManagementsDTO() {
        super();
    }

    public CustomManagementsDTO(Collection<? extends CustomManagementDTO> c) {
        super(c);
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
