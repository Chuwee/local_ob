package es.onebox.common.datasources.catalog.dto.session.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.cache.utils.CacheKey;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class SessionsRequestDTO implements Serializable, CacheKey {

    @JsonProperty("event_id")
    private List<Long> eventIds;
    @JsonProperty("session_id")
    private List<Long> sessionIds;
    private Long limit;
    private Long offset;

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    public List<Long> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Long> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    @Override
    public String generateKey() {
        StringBuffer key = new StringBuffer();
        String eventIds = this.eventIds != null ? this.eventIds.stream().map(String::valueOf).collect(Collectors.joining("_")) : "";
        String sessionIds = this.sessionIds != null ? this.sessionIds.stream().map(String::valueOf).collect(Collectors.joining("_")) : "";
        key.append(eventIds);
        key.append(sessionIds);
        key.append(this.limit);
        key.append(this.offset);

        return key.toString();
    }
}
