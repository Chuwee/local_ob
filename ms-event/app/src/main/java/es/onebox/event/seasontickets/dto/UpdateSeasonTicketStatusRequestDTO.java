package es.onebox.event.seasontickets.dto;

import java.io.Serializable;

public class UpdateSeasonTicketStatusRequestDTO implements Serializable {

    private static final long serialVersionUID = 1676947916912452271L;

    private SeasonTicketStatusDTO status;

    public SeasonTicketStatusDTO getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketStatusDTO status) {
        this.status = status;
    }
}
