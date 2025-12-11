package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serial;
import java.io.Serializable;

public class EventSessionSettings implements Serializable {
    @Serial
    private static final long serialVersionUID = -4469788244053778002L;
    private Boolean showPriceFrom;

    public Boolean getShowPriceFrom() {
        return showPriceFrom;
    }

    public void setShowPriceFrom(Boolean showPriceFrom) {
        this.showPriceFrom = showPriceFrom;
    }
}
