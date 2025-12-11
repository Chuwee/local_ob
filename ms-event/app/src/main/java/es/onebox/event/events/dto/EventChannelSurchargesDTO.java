package es.onebox.event.events.dto;

import es.onebox.event.surcharges.dto.SurchargesDTO;

import java.io.Serial;
import java.io.Serializable;

public class EventChannelSurchargesDTO extends SurchargesDTO implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;
    private Boolean enabledRanges;

    public Boolean getEnabledRanges() {
        return enabledRanges;
    }

    public void setEnabledRanges(Boolean enabledRanges) {
        this.enabledRanges = enabledRanges;
    }
}
