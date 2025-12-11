package es.onebox.mgmt.events.dto;

import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class UpdateEventRateListDTO extends ArrayList<UpdateEventRateDTO> {

    @Serial
    private static final long serialVersionUID = -2876836488585241328L;

    @Valid
    public List<UpdateEventRateDTO> getRates() {
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
