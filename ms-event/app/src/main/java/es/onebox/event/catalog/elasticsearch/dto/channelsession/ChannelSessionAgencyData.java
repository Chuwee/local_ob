package es.onebox.event.catalog.elasticsearch.dto.channelsession;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.onebox.elasticsearch.annotation.ElasticRepository;
import es.onebox.event.catalog.elasticsearch.dto.BaseEventData;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

@ElasticRepository(indexName = EventDataUtils.EVENT_INDEX, queryLimit = 500000)
public class ChannelSessionAgencyData extends BaseEventData {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private Boolean mustBeIndexed;
    private ChannelSessionAgency channelSessionAgency;

    public ChannelSessionAgency getChannelSessionAgency() {
        return channelSessionAgency;
    }

    public void setChannelSessionAgency(ChannelSessionAgency channelSessionAgency) {
        this.channelSessionAgency = channelSessionAgency;
    }

    public void setMustBeIndexed(Boolean mustBeIndexed) {
        this.mustBeIndexed = mustBeIndexed;
    }

    public Boolean getMustBeIndexed() {
        return mustBeIndexed;
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
