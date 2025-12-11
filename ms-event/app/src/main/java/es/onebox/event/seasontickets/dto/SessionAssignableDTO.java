package es.onebox.event.seasontickets.dto;

import java.io.Serializable;

public class SessionAssignableDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean assignable;
    private SessionAssignableReason reason;

    public Boolean getAssignable() {
        return assignable;
    }

    public void setAssignable(Boolean assignable) {
        this.assignable = assignable;
    }

    public SessionAssignableReason getReason() {
        return reason;
    }

    public void setReason(SessionAssignableReason reason) {
        this.reason = reason;
    }
}
