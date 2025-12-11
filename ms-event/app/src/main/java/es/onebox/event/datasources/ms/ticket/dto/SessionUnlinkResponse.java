package es.onebox.event.datasources.ms.ticket.dto;

import java.io.Serializable;

import es.onebox.event.datasources.ms.ticket.enums.SessionUnlinkReason;

public class SessionUnlinkResponse implements Serializable {

    private static final long serialVersionUID = -8157449190314827457L;

    private Boolean result;
    private SessionUnlinkReason reason;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public SessionUnlinkReason getReason() {
        return reason;
    }

    public void setReason(SessionUnlinkReason reason) {
        this.reason = reason;
    }
}
