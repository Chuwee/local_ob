package es.onebox.event.seasontickets.dto.redemption;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketRedemption implements Serializable {

    private Boolean enabled;
    private List<Long> excludedSessions;
    private RedemptionExpiration expiration;

    public SeasonTicketRedemption() {
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

    public RedemptionExpiration getExpiration() {
        return expiration;
    }

    public void setExpiration(RedemptionExpiration expiration) {
        this.expiration = expiration;
    }
}
