package es.onebox.event.catalog.elasticsearch.dto.channelsession;

import es.onebox.elasticsearch.annotation.ElasticRepository;
import es.onebox.event.catalog.elasticsearch.dto.BaseEventData;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serial;

@ElasticRepository(indexName = EventDataUtils.EVENT_INDEX, queryLimit = 500000)
public class ChannelSessionData extends BaseEventData {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private Boolean mustBeIndexed;
    private ChannelSession channelSession;

    public ChannelSession getChannelSession() {
        return channelSession;
    }

    public void setChannelSession(ChannelSession channelSession) {
        this.channelSession = channelSession;
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
