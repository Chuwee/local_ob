package es.onebox.mgmt.loyaltypoints.seasontickets.dto;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SessionLoyaltyPointsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long sessionId;
    private Integer transfer;
    private Integer attendance;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getTransfer() {
        return transfer;
    }

    public void setTransfer(Integer transfer) {
        this.transfer = transfer;
    }

    public Integer getAttendance() { return attendance; }

    public void setAttendance(Integer attendance) { this.attendance = attendance; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}