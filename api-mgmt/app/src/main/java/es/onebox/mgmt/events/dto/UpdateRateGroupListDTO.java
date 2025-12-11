package es.onebox.mgmt.events.dto;

import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class UpdateRateGroupListDTO extends ArrayList<UpdateRateGroupDTO> {

    @Serial
    private static final long serialVersionUID = 4133405197671407397L;

    @Valid
    public List<UpdateRateGroupDTO> getRatesGroup() {
        return this;
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
