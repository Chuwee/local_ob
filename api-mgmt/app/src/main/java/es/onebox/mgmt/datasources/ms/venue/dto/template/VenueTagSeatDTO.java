package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serial;

public class VenueTagSeatDTO extends VenueTagDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    private VenueTagStatus status;
    private Long blockingReason;
    private Long quota;

    public VenueTagStatus getStatus() {
        return status;
    }

    public void setStatus(VenueTagStatus status) {
        this.status = status;
    }

    public Long getBlockingReason() {
        return blockingReason;
    }

    public void setBlockingReason(Long blockingReason) {
        this.blockingReason = blockingReason;
    }

    public Long getQuota() {
        return quota;
    }

    public void setQuota(Long quota) {
        this.quota = quota;
    }

}
