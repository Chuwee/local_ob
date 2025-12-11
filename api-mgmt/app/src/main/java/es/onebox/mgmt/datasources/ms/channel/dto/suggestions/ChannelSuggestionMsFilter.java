package es.onebox.mgmt.datasources.ms.channel.dto.suggestions;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.mgmt.channels.suggestions.enums.SuggestionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class ChannelSuggestionMsFilter extends BaseRequestFilter {
    @Serial
    private static final long serialVersionUID = 3996359693255573174L;

    private String q;
    private Boolean published;
    private List<Long> sessionIds;
    private List<Long> eventIds;
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
