package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;

public class SessionLoyaltyPoints  implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long sessionId;
    private Integer transfer;
    private Integer attendance;

    public Long getSessionId() { return sessionId; }

    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public Integer getTransfer() { return transfer; }

    public void setTransfer(Integer transfer) { this.transfer = transfer; }

    public Integer getAttendance() { return attendance; }

    public void setAttendance(Integer attendance) { this.attendance = attendance; }
}