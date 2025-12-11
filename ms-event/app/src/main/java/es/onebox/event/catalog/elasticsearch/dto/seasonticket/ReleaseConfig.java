package es.onebox.event.catalog.elasticsearch.dto.seasonticket;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ReleaseConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 752120727338756091L;

    private Boolean enabled;
    private Integer releaseMaxDelayTime;
    private Integer releaseMinDelayTime;
    private Integer recoveryMaxDelayTime;
    private Integer maxReleases;
    private Double percentage;
    private Double limit;
    private List<Long> excludedSessions;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getReleaseMaxDelayTime() {
        return releaseMaxDelayTime;
    }

    public void setReleaseMaxDelayTime(Integer releaseMaxDelayTime) {
        this.releaseMaxDelayTime = releaseMaxDelayTime;
    }

    public Integer getReleaseMinDelayTime() {
        return releaseMinDelayTime;
    }

    public void setReleaseMinDelayTime(Integer releaseMinDelayTime) {
        this.releaseMinDelayTime = releaseMinDelayTime;
    }

    public Integer getRecoveryMaxDelayTime() {
        return recoveryMaxDelayTime;
    }

    public void setRecoveryMaxDelayTime(Integer recoveryMaxDelayTime) {
        this.recoveryMaxDelayTime = recoveryMaxDelayTime;
    }

    public Integer getMaxReleases() {
        return maxReleases;
    }

    public void setMaxReleases(Integer maxReleases) {
        this.maxReleases = maxReleases;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Double getLimit() {
        return limit;
    }

    public void setLimit(Double limit) {
        this.limit = limit;
    }

    public List<Long> getExcludedSessions() {
        return excludedSessions;
    }

    public void setExcludedSessions(List<Long> excludedSessions) {
        this.excludedSessions = excludedSessions;
    }
}