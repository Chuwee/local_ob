package es.onebox.event.events.domain.eventconfig;

import java.io.Serial;
import java.io.Serializable;

public class EventSessionSettings implements Serializable {
    @Serial
    private static final long serialVersionUID = -8156987950326756867L;

    private Boolean showPriceFrom;

    public Boolean getShowPriceFrom() {
        return showPriceFrom;
    }

    public void setShowPriceFrom(Boolean showPriceFrom) {
        this.showPriceFrom = showPriceFrom;
    }
}
