package es.onebox.event.seasontickets.dto.releaseseat;

import jakarta.validation.constraints.Min;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SeasonTicketReleaseSeatDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4900488084349192488L;

    private Integer releaseMaxDelayTime;
    private Integer recoverMaxDelayTime;
    @Min(0)
    private Double customerPercentage;
    private List<Long> excludedSessions;
    private Integer maxReleases;
    private Boolean maxReleasesEnabled;
    private EarningsLimitDTO earningsLimit;
    private Integer releaseMinDelayTime;
    private Boolean skipNotifications;

    public Integer getReleaseMaxDelayTime() {
        return releaseMaxDelayTime;
    }

    public void setReleaseMaxDelayTime(Integer releaseMaxDelayTime) {
        this.releaseMaxDelayTime = releaseMaxDelayTime;
    }

    public Integer getRecoverMaxDelayTime() {
        return recoverMaxDelayTime;
    }

    public void setRecoverMaxDelayTime(Integer recoverMaxDelayTime) {
        this.recoverMaxDelayTime = recoverMaxDelayTime;
    }

    public @Min(0) Double getCustomerPercentage() {
        return customerPercentage;
    }

    public void setCustomerPercentage(@Min(0) Double customerPercentage) {
        this.customerPercentage = customerPercentage;
    }

    public List<Long> getExcludedSessions() {
        return excludedSessions;
    }

    public void setExcludedSessions(List<Long> excludedSessions) {
        this.excludedSessions = excludedSessions;
    }

    public Integer getMaxReleases() {
        return maxReleases;
    }

    public void setMaxReleases(Integer maxReleases) {
        this.maxReleases = maxReleases;
    }

    public Boolean getMaxReleasesEnabled() {
        return maxReleasesEnabled;
    }

    public void setMaxReleasesEnabled(Boolean maxReleasesEnabled) {
        this.maxReleasesEnabled = maxReleasesEnabled;
    }

    public EarningsLimitDTO getEarningsLimit() {
        return earningsLimit;
    }

    public void setEarningsLimit(EarningsLimitDTO earningsLimit) {
        this.earningsLimit = earningsLimit;
    }

    public Integer getReleaseMinDelayTime() { return releaseMinDelayTime; }

    public void setReleaseMinDelayTime(Integer releaseMinDelayTime) { this.releaseMinDelayTime = releaseMinDelayTime; }

    public Boolean getSkipNotifications() {
        return skipNotifications;
    }

    public void setSkipNotifications(Boolean skipNotifications) {
        this.skipNotifications = skipNotifications;
    }
}
