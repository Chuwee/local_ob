package es.onebox.mgmt.datasources.ms.event.dto.event;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EventChangeSeat implements Serializable {

    @Serial
    private static final long serialVersionUID = 2622604629279188046L;

    private EventChangeSeatExpiry eventChangeSeatExpiry;
    private ChangeSeatChangeType changeType;
    private ChangeSeatNewTicketSelection newTicketSelection;
    private ChangeSeatReallocationChannel reallocationChannel;

    public EventChangeSeatExpiry getEventChangeSeatExpiry() {
        return eventChangeSeatExpiry;
    }

    public void setEventChangeSeatExpiry(EventChangeSeatExpiry eventChangeSeatExpiry) {
        this.eventChangeSeatExpiry = eventChangeSeatExpiry;
    }

    public ChangeSeatChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeSeatChangeType changeType) {
        this.changeType = changeType;
    }

    public ChangeSeatNewTicketSelection getNewTicketSelection() {
        return newTicketSelection;
    }

    public void setNewTicketSelection(ChangeSeatNewTicketSelection newTicketSelection) {
        this.newTicketSelection = newTicketSelection;
    }

    public ChangeSeatReallocationChannel getReallocationChannel() {
        return reallocationChannel;
    }

    public void setReallocationChannel(ChangeSeatReallocationChannel reallocationChannel) {
        this.reallocationChannel = reallocationChannel;
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
