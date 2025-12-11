package es.onebox.mgmt.seasontickets.dto.releaseseat;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SeasonTicketReleaseSeatConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3924908016272083760L;

    @Positive
    @JsonProperty("release_min_delay_time")
    private Integer releaseSeatMinDelayTime;
    @JsonProperty("release_max_delay_time")
    private Integer releaseSeatMaxDelayTime;
    @JsonProperty("enable_release_delay")
    private Boolean enableReleaseDelay;

    @JsonProperty("enable_recover_delay")
    private Boolean enableRecoverDelay;
    @JsonProperty("recover_max_delay_time")
    private Integer recoverReleasedSeatMaxDelayTime;

    @Positive
    @JsonProperty("customer_percentage")
    private Double customerPercentage;
    @JsonProperty("excluded_sessions")
    private List<Long>  excludedSessions;
    @Positive
    @JsonProperty("max_releases")
    private Integer maxReleases;
    @JsonProperty("enable_max_releases")
    private Boolean maxReleasesEnabled;
    @JsonProperty("earnings_limit")
    private EarningsLimitDTO earningsLimit;


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

    public List<Long> getExcludedSessions() {
        return excludedSessions;
    }

    public void setExcludedSessions(List<Long> excludedSessions) {
        this.excludedSessions = excludedSessions;
    }

    public Integer getMaxReleases() {return maxReleases;}

    public void setMaxReleases(Integer maxReleases) {this.maxReleases = maxReleases;}

    public Boolean getMaxReleasesEnabled() {
        return maxReleasesEnabled;
    }

    public void setMaxReleasesEnabled(Boolean maxReleasesEnabled) {
        this.maxReleasesEnabled = maxReleasesEnabled;
    }

    public EarningsLimitDTO getEarningsLimit() {
        return earningsLimit;
    }

    public void setEarningsLimit(EarningsLimitDTO earningsLimitDTO) {
        this.earningsLimit = earningsLimitDTO;
    }

    public Integer getReleaseSeatMinDelayTime() { return releaseSeatMinDelayTime; }

    public void setReleaseSeatMinDelayTime(Integer releaseSeatMinDelayTime) {
        this.releaseSeatMinDelayTime = releaseSeatMinDelayTime;
    }

    public Boolean getEnableReleaseDelay() {
        return enableReleaseDelay;
    }

    public void setEnableReleaseDelay(Boolean enableReleaseDelay) {
        this.enableReleaseDelay = enableReleaseDelay;
    }

    public Boolean getEnableRecoverDelay() {
        return enableRecoverDelay;
    }

    public void setEnableRecoverDelay(Boolean enableRecoverDelay) {
        this.enableRecoverDelay = enableRecoverDelay;
    }
}