package es.onebox.event.events.dto;

import es.onebox.event.catalog.dto.ChangeSeatChangeType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EventChangeSeatDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2622604629279188046L;

    @Valid
    private EventChangeSeatExpiryDTO eventChangeSeatExpiry;

    @NotNull
    private ChangeSeatChangeType changeType;

    @Valid
    @NotNull
    private ChangeSeatNewTicketSelectionDTO newTicketSelection;

    @Valid
    @NotNull
    private ReallocationChannelDTO reallocationChannel;

    public EventChangeSeatExpiryDTO getEventChangeSeatExpiry() {
        return eventChangeSeatExpiry;
    }

    public void setEventChangeSeatExpiry(EventChangeSeatExpiryDTO eventChangeSeatExpiry) {
        this.eventChangeSeatExpiry = eventChangeSeatExpiry;
    }

    public ChangeSeatChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeSeatChangeType changeType) {
        this.changeType = changeType;
    }

    public ChangeSeatNewTicketSelectionDTO getNewTicketSelection() {
        return newTicketSelection;
    }

    public void setNewTicketSelection(ChangeSeatNewTicketSelectionDTO newTicketSelection) {
        this.newTicketSelection = newTicketSelection;
    }

    public ReallocationChannelDTO getReallocationChannel() {
        return reallocationChannel;
    }

    public void setReallocationChannel(ReallocationChannelDTO reallocationChannel) {
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
