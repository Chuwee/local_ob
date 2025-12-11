package es.onebox.mgmt.seasontickets.dto.redemption;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketRedemptionConfigDTO implements Serializable {

    @JsonProperty("enabled")
    private Boolean enabled;
    @JsonProperty("excluded_sessions")
    private List<Long> excludedSessions;
    @JsonProperty("expiration")
    private RedemptionExpirationDTO expiration;

    public SeasonTicketRedemptionConfigDTO() {
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Long> getExcludedSessions() {
        return excludedSessions;
    }

    public void setExcludedSessions(List<Long> excludedSessions) {
        this.excludedSessions = excludedSessions;
    }

    public RedemptionExpirationDTO getExpiration() {
        return expiration;
    }

    public void setExpiration(RedemptionExpirationDTO expiration) {
        this.expiration = expiration;
    }
}
