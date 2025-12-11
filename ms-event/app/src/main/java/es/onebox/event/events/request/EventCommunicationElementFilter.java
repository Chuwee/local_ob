package es.onebox.event.events.request;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.communicationelements.dto.CommunicationElementFilter;

public class EventCommunicationElementFilter extends CommunicationElementFilter<EventTagType> {

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
