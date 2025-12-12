package es.onebox.internal.utils.progress.model;

import es.onebox.internal.utils.progress.enums.EventMessageType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class AutomaticSalesMessage extends ProgressMessage {

    @Serial
    private static final long serialVersionUID = -1130679496330394467L;
    private Long eventId;
    private Long entityId;
    private Long sessionId;
    private EventMessageType type;

    public AutomaticSalesMessage(String messageName) {
        super(messageName);
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public EventMessageType getType() {
        return type;
    }

    public void setType(EventMessageType type) {
        this.type = type;
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
