package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class EventChangeSeatSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7394821382458079775L;
    @NotNull(message = "enable cannot be null")
    private Boolean enable;

    @Valid
    @JsonProperty("event_change_seat_expiry")
    private EventChangeSeatExpiryDTO eventChangeSeatExpiry;

    @NotNull
    @JsonProperty("change_type")
    private ChangeSeatChangeTypeDTO changeType;

    @Valid
    @NotNull(message = "ticket_selection cannot be null")
    @JsonProperty("ticket_selection")
    private ChangeSeatNewTicketSelectionDTO newTicketSelection;

    @Valid
    @NotNull(message = "reallocation_channel cannot be null")
    @JsonProperty("reallocation_channel")
    private ChangeSeatReallocationChannelDTO reallocationChannel;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public EventChangeSeatExpiryDTO getEventChangeSeatExpiry() {
        return eventChangeSeatExpiry;
    }

    public void setEventChangeSeatExpiry(EventChangeSeatExpiryDTO eventChangeSeatExpiry) {
        this.eventChangeSeatExpiry = eventChangeSeatExpiry;
    }

    public ChangeSeatNewTicketSelectionDTO getNewTicketSelection() {
        return newTicketSelection;
    }

    public void setNewTicketSelection(ChangeSeatNewTicketSelectionDTO newTicketSelection) {
        this.newTicketSelection = newTicketSelection;
    }

    public ChangeSeatChangeTypeDTO getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeSeatChangeTypeDTO changeTypeDTO) {
        this.changeType = changeTypeDTO;
    }

    public ChangeSeatReallocationChannelDTO getReallocationChannel() {
        return reallocationChannel;
    }

    public void setReallocationChannel(ChangeSeatReallocationChannelDTO reallocationChannel) {
        this.reallocationChannel = reallocationChannel;
    }
}
