package es.onebox.event.seasontickets.dto;

import java.io.Serializable;

public class AssignSessionResponseDTO implements Serializable {

    private static final long serialVersionUID = 1676947916912452271L;

    private Boolean result;
    private AssignSessionReason reason;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public AssignSessionReason getReason() {
        return reason;
    }

    public void setReason(AssignSessionReason reason) {
        this.reason = reason;
    }
}
