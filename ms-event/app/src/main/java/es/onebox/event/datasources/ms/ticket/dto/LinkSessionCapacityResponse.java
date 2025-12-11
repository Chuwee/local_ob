package es.onebox.event.datasources.ms.ticket.dto;

import java.io.Serializable;

import es.onebox.event.datasources.ms.ticket.enums.SessionCompatibilityValidationReason;

public class LinkSessionCapacityResponse implements Serializable {

    private static final long serialVersionUID = -8879144340199620058L;
    private Boolean result;
    private SessionCompatibilityValidationReason reason;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public SessionCompatibilityValidationReason getReason() {
        return reason;
    }

    public void setReason(SessionCompatibilityValidationReason reason) {
        this.reason = reason;
    }
}
