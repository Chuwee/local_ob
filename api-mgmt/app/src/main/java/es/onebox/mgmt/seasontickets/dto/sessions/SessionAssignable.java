package es.onebox.mgmt.seasontickets.dto.sessions;

import es.onebox.mgmt.seasontickets.enums.SessionAssignableReason;

import java.io.Serializable;

public class SessionAssignable implements Serializable {

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
