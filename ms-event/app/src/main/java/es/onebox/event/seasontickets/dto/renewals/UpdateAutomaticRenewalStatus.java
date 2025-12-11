package es.onebox.event.seasontickets.dto.renewals;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class UpdateAutomaticRenewalStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = -7768360811809165299L;

    @NotNull
    private SeasonTicketAutomaticRenewalStatus status;

    public SeasonTicketAutomaticRenewalStatus getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketAutomaticRenewalStatus status) {
        this.status = status;
    }
}
