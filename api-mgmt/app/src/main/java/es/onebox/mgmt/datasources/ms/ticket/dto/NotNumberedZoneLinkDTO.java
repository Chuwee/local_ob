package es.onebox.mgmt.datasources.ms.ticket.dto;

import java.io.Serializable;

public class NotNumberedZoneLinkDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer capacity;

    private LinkSeatStatus fromStatus;
    private Long fromBlockingReason;

    private LinkSeatStatus toStatus;
    private Long toBlockingReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public LinkSeatStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(LinkSeatStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public Long getFromBlockingReason() {
        return fromBlockingReason;
    }

    public void setFromBlockingReason(Long fromBlockingReason) {
        this.fromBlockingReason = fromBlockingReason;
    }

    public LinkSeatStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(LinkSeatStatus toStatus) {
        this.toStatus = toStatus;
    }

    public Long getToBlockingReason() {
        return toBlockingReason;
    }

    public void setToBlockingReason(Long toBlockingReason) {
        this.toBlockingReason = toBlockingReason;
    }
}
