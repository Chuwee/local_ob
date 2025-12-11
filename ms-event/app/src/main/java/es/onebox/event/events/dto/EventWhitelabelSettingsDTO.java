package es.onebox.event.events.dto;

import java.io.Serial;
import java.io.Serializable;

public class EventWhitelabelSettingsDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = 4892823242891375577L;

    private EventSessionSelectionDTO sessionSelection;
    private EventSessionSettingsDTO sessionSettings;
    private EventSeatSelectionDTO seatSelection;

    public EventWhitelabelSettingsDTO() {
    }

    public EventWhitelabelSettingsDTO(EventSessionSelectionDTO sessionSelection, EventSessionSettingsDTO sessionSettings, EventSeatSelectionDTO seatSelection) {
        this.sessionSelection = sessionSelection;
        this.sessionSettings = sessionSettings;
        this.seatSelection = seatSelection;
    }

    public EventSessionSelectionDTO getSessionSelection() {
        return sessionSelection;
    }

    public void setSessionSelection(EventSessionSelectionDTO sessionSelection) {
        this.sessionSelection = sessionSelection;
    }

    public EventSessionSettingsDTO getSessionSettings() {
        return sessionSettings;
    }

    public void setSessionSettings(EventSessionSettingsDTO sessionSettings) {
        this.sessionSettings = sessionSettings;
    }

    public EventSeatSelectionDTO getSeatSelection() {
        return seatSelection;
    }

    public void setSeatSelection(EventSeatSelectionDTO seatSelection) {
        this.seatSelection = seatSelection;
    }
}
