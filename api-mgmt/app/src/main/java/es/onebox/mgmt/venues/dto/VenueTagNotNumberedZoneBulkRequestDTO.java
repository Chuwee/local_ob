package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.venues.enums.VenueTagStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class VenueTagNotNumberedZoneBulkRequestDTO extends VenueTagNotNumberedZoneRequestDTO {

    //For global assignations
    private VenueTagStatus status;
    @JsonProperty("blocking_reason")
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
