package es.onebox.common.datasources.ms.event.dto;

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
