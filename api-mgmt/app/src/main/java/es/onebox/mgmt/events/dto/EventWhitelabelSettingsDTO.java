package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EventWhitelabelSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7936592002743931848L;

    @JsonProperty("ui_settings")
    private EventUISettingsDTO eventUISettings;

    public EventUISettingsDTO getEventUISettings() {
        return eventUISettings;
    }

    public void setEventUISettings(EventUISettingsDTO eventUISettings) {
        this.eventUISettings = eventUISettings;
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
