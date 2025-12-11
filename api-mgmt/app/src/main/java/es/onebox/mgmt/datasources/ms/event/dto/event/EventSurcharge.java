package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.datasources.common.dto.Surcharge;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EventSurcharge extends Surcharge implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private EventSurchargeLimit limit;
    private Boolean allowChannelUseAlternativeCharges;

    public EventSurchargeLimit getLimit() {
        return limit;
    }

    public void setLimit(EventSurchargeLimit limit) {
        this.limit = limit;
    }

    public Boolean getAllowChannelUseAlternativeCharges() {
        return allowChannelUseAlternativeCharges;
    }

    public void setAllowChannelUseAlternativeCharges(Boolean allowChannelUseAlternativeCharges) {
        this.allowChannelUseAlternativeCharges = allowChannelUseAlternativeCharges;
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
