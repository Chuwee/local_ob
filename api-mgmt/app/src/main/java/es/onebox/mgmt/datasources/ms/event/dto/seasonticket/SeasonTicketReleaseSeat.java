package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketReleaseSeat implements Serializable {
    private static final long serialVersionUID = -5083810816453509198L;

    private Integer releaseMaxDelayTime;
    private Integer recoverMaxDelayTime;
    private Double customerPercentage;
    private List<Long> excludedSessions;
    private Integer maxReleases;
    private Boolean maxReleasesEnabled;
    private EarningsLimit earningsLimit;
    private Integer releaseMinDelayTime;

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

    public Double getCustomerPercentage() {
        return customerPercentage;
    }

    public void setCustomerPercentage(Double customerPercentage) {
        this.customerPercentage = customerPercentage;
    }

    public List<Long> getExcludedSessions() {
        return excludedSessions;
    }

    public void setExcludedSessions(List<Long> excludedSessions) {
        this.excludedSessions = excludedSessions;
    }

    public Integer getMaxReleases(){ return maxReleases;}

    public void setMaxReleases(Integer maxReleases) {
        this.maxReleases = maxReleases;
    }

    public Boolean getMaxReleasesEnabled() {
        return maxReleasesEnabled;
    }

    public void setMaxReleasesEnabled(Boolean maxReleasesEnabled) {this.maxReleasesEnabled = maxReleasesEnabled;}

    public EarningsLimit getEarningsLimit() {
        return earningsLimit;
    }

    public void setEarningsLimit(EarningsLimit earningsLimit) {
        this.earningsLimit = earningsLimit;
    }

    public Integer getReleaseMinDelayTime() { return releaseMinDelayTime; }

    public void setReleaseMinDelayTime(Integer releaseMinDelayTime) { this.releaseMinDelayTime = releaseMinDelayTime; }
}