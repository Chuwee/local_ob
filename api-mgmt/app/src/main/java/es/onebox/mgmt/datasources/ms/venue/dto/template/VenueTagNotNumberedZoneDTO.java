package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serial;
import java.util.List;

public class VenueTagNotNumberedZoneDTO extends VenueTagDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<VenueTagCounterDTO> statusCounters;
    private List<VenueTagCounterDTO> blockingReasonCounters;
    private List<VenueTagCounterDTO> quotaCounters;

    //For global assignations
    private VenueTagStatus status;
    private Long blockingReason;
    private Long quota;

    public List<VenueTagCounterDTO> getStatusCounters() {
        return statusCounters;
    }

    public void setStatusCounters(List<VenueTagCounterDTO> statusCounters) {
        this.statusCounters = statusCounters;
    }

    public List<VenueTagCounterDTO> getBlockingReasonCounters() {
        return blockingReasonCounters;
    }

    public void setBlockingReasonCounters(List<VenueTagCounterDTO> blockingReasonCounters) {
        this.blockingReasonCounters = blockingReasonCounters;
    }

    public List<VenueTagCounterDTO> getQuotaCounters() {
        return quotaCounters;
    }

    public void setQuotaCounters(List<VenueTagCounterDTO> quotaCounters) {
        this.quotaCounters = quotaCounters;
    }

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
