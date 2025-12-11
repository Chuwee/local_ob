package es.onebox.event.catalog.elasticsearch.context;

import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ChannelSessionForOccupationIndexation extends ForOccupationIndexation<ChannelSession> {

    @Serial
    private static final long serialVersionUID = 1L;


    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
