package es.onebox.mgmt.seasontickets.dto;

import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

@NotEmpty
public class UpdateSeasonTicketPricesRequestListDTO extends ArrayList<UpdateSeasonTicketPriceRequestDTO> {

    private static final long serialVersionUID = -6072087988586814647L;

    public List<UpdateSeasonTicketPriceRequestDTO> getPrices() {
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
