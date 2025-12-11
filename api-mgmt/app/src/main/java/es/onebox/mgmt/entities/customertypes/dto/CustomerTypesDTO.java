package es.onebox.mgmt.entities.customertypes.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class CustomerTypesDTO extends ArrayList<CustomerTypeDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 6293935618885061980L;

    public CustomerTypesDTO(Collection<? extends CustomerTypeDTO> c) {
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

