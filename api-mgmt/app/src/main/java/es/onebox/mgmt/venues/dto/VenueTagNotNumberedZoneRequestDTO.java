package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class VenueTagNotNumberedZoneRequestDTO extends BaseVenueTagDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("status_counters")
    private List<VenueTagStatusCounterDTO> statusCounters;

    @JsonProperty("blocking_reason_counters")
    private List<VenueTagBlockingReasonCounterDTO> blockingReasonCounters;

    @JsonProperty("quota_counters")
    @Valid
    private List<VenueTagQuotaCounterDTO> quotaCounters;

    public List<VenueTagStatusCounterDTO> getStatusCounters() {
        return statusCounters;
    }

    public void setStatusCounters(List<VenueTagStatusCounterDTO> statusCounters) {
        this.statusCounters = statusCounters;
    }

    public List<VenueTagBlockingReasonCounterDTO> getBlockingReasonCounters() {
        return blockingReasonCounters;
    }

    public void setBlockingReasonCounters(List<VenueTagBlockingReasonCounterDTO> blockingReasonCounters) {
        this.blockingReasonCounters = blockingReasonCounters;
    }

    public List<VenueTagQuotaCounterDTO> getQuotaCounters() {
        return quotaCounters;
    }

    public void setQuotaCounters(List<VenueTagQuotaCounterDTO> quotaCounters) {
        this.quotaCounters = quotaCounters;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
