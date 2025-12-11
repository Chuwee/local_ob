package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SessionPackRefundConditionsUpdateDTO implements Serializable {

    private static final long serialVersionUID = 6929877205857078122L;

    @JsonProperty("session_id")
    private Long sessionId;
    @JsonProperty("price_percentage_values")
    private List<PricePercentageValuesUpdateDTO> pricePercentageValues;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<PricePercentageValuesUpdateDTO> getPricePercentageValues() {
        return pricePercentageValues;
    }

    public void setPricePercentageValues(List<PricePercentageValuesUpdateDTO> pricePercentageValues) {
        this.pricePercentageValues = pricePercentageValues;
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
