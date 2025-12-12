package es.onebox.internal.automaticrenewals.eip.progress;

import es.onebox.internal.utils.progress.enums.EventMessageType;
import es.onebox.internal.utils.progress.model.ProgressMessage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class AutomaticRenewalsProgressMessage extends ProgressMessage {

    @Serial
    private static final long serialVersionUID = 5928281548062244926L;

    private Long seasonTicketId;
    private Long entityId;
    private EventMessageType type;

    public AutomaticRenewalsProgressMessage(String messageName) {
        super(messageName);
    }

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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