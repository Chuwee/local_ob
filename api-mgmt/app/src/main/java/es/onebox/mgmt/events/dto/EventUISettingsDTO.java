package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EventUISettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1928273695179923377L;

    @JsonProperty("session_selection")
    private EventUISessionSelectionDTO eventUISessionSelection;
    @JsonProperty("seat_selection")
    private EventUISeatSelectionDTO eventUISeatSelection;
    @JsonProperty("session")
    private EventUISessionSettingsDTO eventUISessionSettings;

    public EventUISessionSelectionDTO getEventUISessionSelection() {
        return eventUISessionSelection;
    }

    public void setEventUISessionSelection(EventUISessionSelectionDTO eventUISessionSelection) {
        this.eventUISessionSelection = eventUISessionSelection;
    }
    public EventUISeatSelectionDTO getEventUISeatSelection() { return eventUISeatSelection; }
    public void setEventUISeatSelection(EventUISeatSelectionDTO eventUISeatSelection) {
        this.eventUISeatSelection = eventUISeatSelection;
    }

    public EventUISessionSettingsDTO getEventUISessionSettings() {
        return eventUISessionSettings;
    }

    public void setEventUISessionSettings(EventUISessionSettingsDTO eventUISessionSettings) {
        this.eventUISessionSettings = eventUISessionSettings;
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
