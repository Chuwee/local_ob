package es.onebox.event.seasontickets.dto;

import java.io.Serializable;

public class UnAssignSessionResponseDTO implements Serializable {

    private static final long serialVersionUID = 1676947916912452271L;

    private Boolean result;
    private UnAssignSessionReason reason;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public UnAssignSessionReason getReason() {
        return reason;
    }

    public void setReason(UnAssignSessionReason reason) {
        this.reason = reason;
    }
}
