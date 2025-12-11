package es.onebox.event.sessions.dto;

import java.io.Serializable;

public class DeleteSessionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SeatDeleteStatus status;
    private Integer blockingReasonId;

    public SeatDeleteStatus getStatus() {
        return status;
    }

    public void setStatus(SeatDeleteStatus status) {
        this.status = status;
    }

    public Integer getBlockingReasonId() {
        return blockingReasonId;
    }

    public void setBlockingReasonId(Integer blockingReasonId) {
        this.blockingReasonId = blockingReasonId;
    }

}
