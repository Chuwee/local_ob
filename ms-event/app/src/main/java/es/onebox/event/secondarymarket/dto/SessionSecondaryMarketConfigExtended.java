package es.onebox.event.secondarymarket.dto;

import es.onebox.event.secondarymarket.domain.Commission;
import es.onebox.event.secondarymarket.domain.ResalePrice;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import es.onebox.event.secondarymarket.domain.SessionSecondaryMarketDates;

import java.io.Serial;
import java.io.Serializable;

public class SessionSecondaryMarketConfigExtended implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long eventId;
    private Boolean enabled;
    private ResalePrice price;
    private Commission commission;
    private SessionSecondaryMarketDates dates;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ResalePrice getPrice() {
        return price;
    }

    public void setPrice(ResalePrice price) {
        this.price = price;
    }

    public Commission getCommission() {
        return commission;
    }

    public void setCommission(Commission commission) {
        this.commission = commission;
    }

    public SessionSecondaryMarketDates getDates() {
        return dates;
    }

    public void setDates(SessionSecondaryMarketDates dates) {
        this.dates = dates;
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
