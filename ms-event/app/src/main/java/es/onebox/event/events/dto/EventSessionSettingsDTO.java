package es.onebox.event.events.dto;

import java.io.Serial;
import java.io.Serializable;

public class EventSessionSettingsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5308524790852207436L;

    public EventSessionSettingsDTO(){
    }
    public EventSessionSettingsDTO(Boolean showPriceFrom) {
        this.showPriceFrom = showPriceFrom;
    }

    private Boolean showPriceFrom;

    public Boolean getShowPriceFrom() {
        return showPriceFrom;
    }

    public void setShowPriceFrom(Boolean showPriceFrom) {
        this.showPriceFrom = showPriceFrom;
    }
}
