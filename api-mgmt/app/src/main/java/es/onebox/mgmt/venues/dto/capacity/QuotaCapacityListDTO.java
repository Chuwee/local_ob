package es.onebox.mgmt.venues.dto.capacity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;

public class QuotaCapacityListDTO extends ArrayList<QuotaCapacityDTO> {

    @Serial
    private static final long serialVersionUID = -3458342583089013100L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
