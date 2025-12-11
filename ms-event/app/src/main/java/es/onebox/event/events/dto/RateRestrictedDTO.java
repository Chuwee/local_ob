package es.onebox.event.events.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class RateRestrictedDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3572359705249025047L;

    private IdNameDTO rate;
    private EventRateRestrictionsDTO restrictions;

    public IdNameDTO getRate() {
        return rate;
    }

    public void setRate(IdNameDTO rate) {
        this.rate = rate;
    }

    public EventRateRestrictionsDTO getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(EventRateRestrictionsDTO restrictions) {
        this.restrictions = restrictions;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
