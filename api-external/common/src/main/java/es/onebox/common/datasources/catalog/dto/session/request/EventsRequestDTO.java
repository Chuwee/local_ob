package es.onebox.common.datasources.catalog.dto.session.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.cache.utils.CacheKey;
import org.apache.logging.log4j.util.Strings;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class EventsRequestDTO implements Serializable, CacheKey {

    @JsonProperty("event_id")
    private List<Long> eventIds;
    private Long limit;
    private Long offset;

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
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

        return this.limit + "-" + this.offset + "-"+ Strings.join(eventIds, '-');
    }
}
