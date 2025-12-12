package es.onebox.common.datasources.ms.event.dto;

import java.io.Serial;
import java.io.Serializable;

public class UpdateSeasonTicketAutomaticRenewalStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = -3144882603868016008L;

    private String status;

    public UpdateSeasonTicketAutomaticRenewalStatus() {}

    public UpdateSeasonTicketAutomaticRenewalStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
