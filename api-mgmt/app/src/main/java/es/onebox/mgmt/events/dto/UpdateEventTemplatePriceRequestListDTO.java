package es.onebox.mgmt.events.dto;

import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

@NotEmpty
public class UpdateEventTemplatePriceRequestListDTO extends ArrayList<UpdateEventTemplatePriceRequestDTO> {

    private static final long serialVersionUID = 1L;

    public List<UpdateEventTemplatePriceRequestDTO> getPrices() {
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
