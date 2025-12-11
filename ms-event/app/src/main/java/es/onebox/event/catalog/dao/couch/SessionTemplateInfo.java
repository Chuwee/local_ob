package es.onebox.event.catalog.dao.couch;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

public class SessionTemplateInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 5575193337493690994L;

    private List<String> tags;
    private Long sessionId;
    private TemplateInfoStatus status;
    private AggregatedInfo aggregatedInfo;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public TemplateInfoStatus getStatus() {
        return status;
    }

    public void setStatus(TemplateInfoStatus status) {
        this.status = status;
    }

    public AggregatedInfo getAggregatedInfo() {
        return aggregatedInfo;
    }

    public void setAggregatedInfo(AggregatedInfo aggregatedInfo) {
        this.aggregatedInfo = aggregatedInfo;
    }

    @Override
    public boolean equals(Object obj) {
        return reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
