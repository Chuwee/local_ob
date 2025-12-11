package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;
import java.util.List;

public class SessionPackRefundConditionsDTO {

    @JsonProperty("session_id")
    private Long sessionId;
    @JsonProperty("session_name")
    private String sessionName;
    @JsonProperty("session_start_date")
    private ZonedDateTime sessionStartDate;
    @JsonProperty("price_percentage_values")
    private List<PricePercentageValuesDTO> pricePercentageValues;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public ZonedDateTime getSessionStartDate() {
        return sessionStartDate;
    }

    public void setSessionStartDate(ZonedDateTime sessionStartDate) {
        this.sessionStartDate = sessionStartDate;
    }

    public List<PricePercentageValuesDTO> getPricePercentageValues() {
        return pricePercentageValues;
    }

    public void setPricePercentageValues(List<PricePercentageValuesDTO> pricePercentageValues) {
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
