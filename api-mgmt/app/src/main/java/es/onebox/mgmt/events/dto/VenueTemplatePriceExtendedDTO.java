package es.onebox.mgmt.events.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class VenueTemplatePriceExtendedDTO extends VenueTemplatePriceBaseDTO {

    @Serial
    private static final long serialVersionUID = 8559404565640756092L;
    private RateExtendedDTO rate;

    public RateExtendedDTO getRate() {
        return rate;
    }

    public void setRate(RateExtendedDTO rate) {
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
