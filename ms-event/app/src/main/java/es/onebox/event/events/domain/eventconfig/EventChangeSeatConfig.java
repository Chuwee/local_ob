package es.onebox.event.events.domain.eventconfig;

import es.onebox.event.catalog.dto.ChangeSeatChangeType;

import java.io.Serializable;

public class EventChangeSeatConfig implements Serializable {

    private Boolean allowChangeSeat;
    private EventChangeSeatExpiry eventChangeSeatExpiry;
    private ChangeSeatChangeType changeType;
    private ChangeSeatNewTicketSelection newTicketSelection;
    private ReallocationChannel reallocationChannel;

    public Boolean getAllowChangeSeat() {
        return allowChangeSeat;
    }

    public void setAllowChangeSeat(Boolean allowChangeSeat) {
        this.allowChangeSeat = allowChangeSeat;
    }

    public EventChangeSeatExpiry getEventChangeSeatExpiry() {
        return eventChangeSeatExpiry;
    }

    public void setEventChangeSeatExpiry(EventChangeSeatExpiry eventChangeSeatExpiry) {
        this.eventChangeSeatExpiry = eventChangeSeatExpiry;
    }

    public ChangeSeatChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeSeatChangeType changeSeatChangeType) {
        this.changeType = changeSeatChangeType;
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
}
