package es.onebox.event.catalog.elasticsearch.dto.event;

import java.io.Serial;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.elasticsearch.annotation.ElasticRepository;
import es.onebox.event.catalog.elasticsearch.dto.BaseEventData;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;

@ElasticRepository(indexName = EventDataUtils.EVENT_INDEX, queryLimit = 500000)
public class  EventData extends BaseEventData {

    @Serial
    private static final long serialVersionUID = 1L;

    private Event event;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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
