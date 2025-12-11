package es.onebox.event.seasontickets.dto;

import java.io.Serializable;

public class SeasonTicketSessionValidationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean result;
    private SeasonTicketSessionValidationReason reason;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public SeasonTicketSessionValidationReason getReason() {
        return reason;
    }

    public void setReason(SeasonTicketSessionValidationReason reason) {
        this.reason = reason;
    }
}
