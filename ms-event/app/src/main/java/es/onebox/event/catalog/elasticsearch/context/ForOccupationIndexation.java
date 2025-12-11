package es.onebox.event.catalog.elasticsearch.context;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;

import java.io.Serial;

public class ForOccupationIndexation<T extends ChannelSession> extends ChannelSessionPriceZones {

    @Serial
    private static final long serialVersionUID = 1L;

    private T channelSessionIndexed;

    public T getChannelSessionIndexed() {
        return channelSessionIndexed;
    }

    public void setChannelSessionIndexed(T channelSessionIndexed) {
        this.channelSessionIndexed = channelSessionIndexed;
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
