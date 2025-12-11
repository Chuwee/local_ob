package es.onebox.event.datasources.ms.ticket.dto;

import java.io.Serializable;

import es.onebox.event.datasources.ms.ticket.enums.SessionCompatibilityValidationReason;

public class SessionCompatibilityValidationResponse implements Serializable {

    private static final long serialVersionUID = -8157449190314827457L;

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
