package es.onebox.mgmt.channels.suggestions.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.mgmt.channels.suggestions.enums.SuggestionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class ChannelSuggestionFilter extends BaseRequestFilter {
    @Serial
    private static final long serialVersionUID = 3996359693255573174L;

    private String q;
    private Boolean published;
    @JsonProperty("session_id")
    private List<Long> sessionIds;
    @JsonProperty("event_id")
    private List<Long> eventIds;
    @JsonProperty("source_type")
    private SuggestionType sourceType;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public List<Long> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Long> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    public SuggestionType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SuggestionType sourceType) {
        this.sourceType = sourceType;
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
