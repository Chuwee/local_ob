package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;
import java.util.List;

public class UpdateSeasonTicketRedemption implements Serializable {

    private Boolean enabled;
    private List<Long> excludedSessions;
    private RedemptionExpiration expiration;

    public UpdateSeasonTicketRedemption() {
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
