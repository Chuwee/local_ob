package es.onebox.event.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SessionSaleRestrictionDTO implements Serializable {

    private Integer sessionId;
    private List<PriceTypeRestrictionDTO> priceTypeRestrictions;

    public List<PriceTypeRestrictionDTO> getPriceTypeRestrictions() {
        return priceTypeRestrictions;
    }

    public void setPriceTypeRestrictions(List<PriceTypeRestrictionDTO> priceTypeRestrictions) {
        this.priceTypeRestrictions = priceTypeRestrictions;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }
}
