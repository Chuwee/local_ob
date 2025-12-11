package es.onebox.mgmt.datasources.ms.ticket.dto;

import java.io.Serializable;
import java.util.List;

public class SeatLinkDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> ids;
    private LinkSeatStatus toStatus;
    private Long toBlockingReason;
    private Long toQuota;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
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

    public Long getToQuota() {
        return toQuota;
    }

    public void setToQuota(Long toQuota) {
        this.toQuota = toQuota;
    }
}
