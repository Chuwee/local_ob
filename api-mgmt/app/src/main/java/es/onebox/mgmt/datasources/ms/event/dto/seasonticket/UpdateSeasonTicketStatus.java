package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serial;
import java.io.Serializable;

public class UpdateSeasonTicketStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = -6031420805215805963L;

    SeasonTicketStatus status;

    public SeasonTicketStatus getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketStatus status) {
        this.status = status;
    }
}
