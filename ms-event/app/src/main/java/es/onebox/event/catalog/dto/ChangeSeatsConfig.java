package es.onebox.event.catalog.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChangeSeatsConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 6207545906585202089L;

    private Boolean allowChangeSeat;
    private ChangeSeatsExpiry eventChangeSeatExpiry;
    private ChangeSeatChangeType changeType;
    private ChangeSeatNewTicketSelection newTicketSelection;
    private ReallocationChannel reallocationChannel;

    public Boolean getAllowChangeSeat() {
        return allowChangeSeat;
    }

    public void setAllowChangeSeat(Boolean allowChangeSeat) {
        this.allowChangeSeat = allowChangeSeat;
    }

    public ChangeSeatsExpiry getEventChangeSeatExpiry() {
        return eventChangeSeatExpiry;
    }

    public void setEventChangeSeatExpiry(ChangeSeatsExpiry eventChangeSeatExpiry) {
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

    public void setNewTicketSelection(ChangeSeatNewTicketSelection changeSeatNewTicketSelection) {
        this.newTicketSelection = changeSeatNewTicketSelection;
    }

    public ReallocationChannel getReallocationChannel() {
        return reallocationChannel;
    }

    public void setReallocationChannel(ReallocationChannel reallocationChannel) {
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
