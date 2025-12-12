package es.onebox.circuitcat.seats.dto;

import es.onebox.common.datasources.common.dto.SeatStatus;

import java.io.Serializable;

public class UpdateSeatDTO implements Serializable {

    private static final long serialVersionUID = -2073466661014545986L;

    private Long id;
    private Long sessionId;
    private Long blockingReasonId;
    private SeatStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getBlockingReasonId() {
        return blockingReasonId;
    }

    public void setBlockingReasonId(Long blockingReasonId) {
        this.blockingReasonId = blockingReasonId;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }
}
