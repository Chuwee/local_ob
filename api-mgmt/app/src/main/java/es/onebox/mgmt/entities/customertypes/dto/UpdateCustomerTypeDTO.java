package es.onebox.mgmt.entities.customertypes.dto;

import es.onebox.core.serializer.dto.common.CodeNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateCustomerTypeDTO extends CodeNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8809162806156123007L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
