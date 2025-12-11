package es.onebox.event.events.request;

import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.communicationelements.dto.CommunicationElementFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ChannelEventCommunicationElementFilter extends CommunicationElementFilter<EventTagType> {

    private static final long serialVersionUID = 1L;

    private boolean includeAllSessions;

    public boolean isIncludeAllSessions() {
        return includeAllSessions;
    }

    public void setIncludeAllSessions(boolean includeAllSessions) {
        this.includeAllSessions = includeAllSessions;
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
