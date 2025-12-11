package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EventUISessionSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -805668222215036615L;
    @JsonProperty("show_price_from")
    private Boolean showPriceFrom;

    public EventUISessionSettingsDTO() {
    }
    public Boolean getShowPriceFrom() { return showPriceFrom; }
    public void setShowPriceFrom(Boolean showPriceFrom) { this.showPriceFrom = showPriceFrom; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
