package es.onebox.mgmt.events.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class VenueTemplatePriceDTO extends VenueTemplatePriceBaseDTO  implements Serializable {

    @Serial
    private static final long serialVersionUID = 9032839100474273244L;

    private IdNameDTO rate;

    public IdNameDTO getRate() {
        return rate;
    }

    public void setRate(IdNameDTO rate) {
        this.rate = rate;
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
