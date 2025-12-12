package es.onebox.channels.catalog;

import es.onebox.common.datasources.catalog.dto.ChannelEventDetail;
import es.onebox.common.datasources.catalog.dto.session.ChannelSession;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class CatalogEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ChannelEventDetail event;
    private final List<ChannelSession> sessions;

    public CatalogEvent(ChannelEventDetail event, List<ChannelSession> sessions) {
        this.event = event;
        this.sessions = sessions;
    }

    public ChannelEventDetail getEvent() {
        return event;
    }

    public List<ChannelSession> getSessions() {
        return sessions;
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
