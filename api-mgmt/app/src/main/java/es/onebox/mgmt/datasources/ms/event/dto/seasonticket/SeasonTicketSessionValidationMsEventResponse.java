package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;

public class SeasonTicketSessionValidationMsEventResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean result;
    private SeasonTicketSessionValidationMsEventReason reason;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public SeasonTicketSessionValidationMsEventReason getReason() {
        return reason;
    }

    public void setReason(SeasonTicketSessionValidationMsEventReason reason) {
        this.reason = reason;
    }
}
