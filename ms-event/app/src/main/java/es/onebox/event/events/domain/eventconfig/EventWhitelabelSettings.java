package es.onebox.event.events.domain.eventconfig;

import java.io.Serial;
import java.io.Serializable;

public class EventWhitelabelSettings implements Serializable {


    @Serial
    private static final long serialVersionUID = 4892823242891375577L;

    private EventSessionSelection sessionSelection;
    private EventSessionSettings sessionSettings;
    private EventSeatSelection seatSelection;

    public EventWhitelabelSettings() {
    }

    public EventWhitelabelSettings(EventSessionSelection sessionSelection) {
        this.sessionSelection = sessionSelection;
    }

    public EventSessionSelection getSessionSelection() {
        return sessionSelection;
    }

    public void setSessionSelection(EventSessionSelection sessionSelection) {
        this.sessionSelection = sessionSelection;
    }

    public EventSessionSettings getSessionSettings() { return sessionSettings; }
    public void setSessionSettings(EventSessionSettings sessionSettings) {
        this.sessionSettings = sessionSettings;
    }
    public EventSeatSelection getSeatSelection() { return seatSelection; }
    public void setSeatSelection(EventSeatSelection seatSelection) {
        this.seatSelection = seatSelection;
    }
}
