package es.onebox.event.seasontickets.dto;

import es.onebox.event.events.dto.RateDTO;

import java.io.Serial;

public class SeasonTicketRateDTO extends RateDTO {
    @Serial
    private static final long serialVersionUID = 6716110880976705975L;

    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
