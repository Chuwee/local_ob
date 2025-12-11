package es.onebox.event.catalog.elasticsearch.dto.channelevent;

import java.io.Serial;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.elasticsearch.annotation.ElasticRepository;
import es.onebox.event.catalog.elasticsearch.dto.BaseEventData;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;

@ElasticRepository(indexName = EventDataUtils.EVENT_INDEX, queryLimit = 500000)
public class ChannelEventData extends BaseEventData {

    @Serial
    private static final long serialVersionUID = 1L;

    private ChannelEvent channelEvent;

    @JsonIgnore
    private Boolean mustBeIndexed;

    public ChannelEvent getChannelEvent() {
        return channelEvent;
    }

    public void setChannelEvent(ChannelEvent channelEvent) {
        this.channelEvent = channelEvent;
    }

    public Boolean getMustBeIndexed() {
        return mustBeIndexed;
    }

    public void setMustBeIndexed(Boolean mustBeIndexed) {
        this.mustBeIndexed = mustBeIndexed;
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
