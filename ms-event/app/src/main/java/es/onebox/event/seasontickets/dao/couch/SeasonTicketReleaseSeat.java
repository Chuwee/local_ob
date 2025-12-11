package es.onebox.event.seasontickets.dao.couch;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serializable;
import java.util.List;

@CouchDocument
public class SeasonTicketReleaseSeat implements Serializable {
    private static final long serialVersionUID = 8233150484553027316L;

    @Id
    private String seasonTicketId;
    private Integer releaseSeatMaxDelayTime;
    private Integer recoverReleasedSeatMaxDelayTime;
    private Double customerPercentage;
    private EarningsLimit earningsLimit;
    private List<Long> excludedSessions;
    private Integer maxReleases;
    private Boolean maxReleasesEnabled;
    private Integer releaseSeatMinDelayTime;
    private Boolean skipNotifications;

    public String getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(String seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public Integer getReleaseSeatMaxDelayTime() {
        return releaseSeatMaxDelayTime;
    }

    public void setReleaseSeatMaxDelayTime(Integer releaseSeatMaxDelayTime) {
        this.releaseSeatMaxDelayTime = releaseSeatMaxDelayTime;
    }

    public Integer getRecoverReleasedSeatMaxDelayTime() {
        return recoverReleasedSeatMaxDelayTime;
    }

    public void setRecoverReleasedSeatMaxDelayTime(Integer recoverReleasedSeatMaxDelayTime) {
        this.recoverReleasedSeatMaxDelayTime = recoverReleasedSeatMaxDelayTime;
    }

    public Double getCustomerPercentage() {
        return customerPercentage;
    }

    public void setCustomerPercentage(Double customerPercentage) {
        this.customerPercentage = customerPercentage;
    }

    public EarningsLimit getEarningsLimit() {
        return earningsLimit;
    }

    public void setEarningsLimit(EarningsLimit earningsLimit) {
        this.earningsLimit = earningsLimit;
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

    public Integer getReleaseSeatMinDelayTime() { return releaseSeatMinDelayTime; }

    public void setReleaseSeatMinDelayTime(Integer releaseSeatMinDelayTime) {
        this.releaseSeatMinDelayTime = releaseSeatMinDelayTime;
    }

    public Boolean getSkipNotifications() {
        return skipNotifications;
    }

    public void setSkipNotifications(Boolean skipNotifications) {
        this.skipNotifications = skipNotifications;
    }
}
