package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.venue.dto.template.AccessibilityType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VisibilityType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class NotNumberedZoneDTO extends BaseNotNumberedZoneDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("view_id")
    private Long viewId;
    @JsonProperty("status_counters")
    private List<StatusCounterDTO> statusCounters;
    @JsonProperty("blocking_reason_counters")
    private List<BlockingReasonCounterDTO> blockingReasonCounters;
    @JsonProperty("price_type")
    private Integer priceType;
    private Integer quota;
    private VisibilityType visibility;
    private AccessibilityType accessibility;
    private Long gate;
    @JsonProperty("quota_counters")
    private List<QuotaCounterDTO> quotaCounters;

    public Long getViewId() {
        return viewId;
    }

    public void setViewId(Long viewId) {
        this.viewId = viewId;
    }

    public List<StatusCounterDTO> getStatusCounters() {
        return statusCounters;
    }

    public void setStatusCounters(List<StatusCounterDTO> statusCounters) {
        this.statusCounters = statusCounters;
    }

    public List<BlockingReasonCounterDTO> getBlockingReasonCounters() {
        return blockingReasonCounters;
    }

    public void setBlockingReasonCounters(List<BlockingReasonCounterDTO> blockingReasonCounters) {
        this.blockingReasonCounters = blockingReasonCounters;
    }

    public Integer getPriceType() {
        return priceType;
    }

    public void setPriceType(Integer priceType) {
        this.priceType = priceType;
    }

    public Integer getQuota() {
        return quota;
    }

    public void setQuota(Integer quota) {
        this.quota = quota;
    }

    public VisibilityType getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityType visibility) {
        this.visibility = visibility;
    }

    public AccessibilityType getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(AccessibilityType accessibility) {
        this.accessibility = accessibility;
    }

    public Long getGate() {
        return gate;
    }

    public void setGate(Long gate) {
        this.gate = gate;
    }

    public List<QuotaCounterDTO> getQuotaCounters() {
        return quotaCounters;
    }

    public void setQuotaCounters(List<QuotaCounterDTO> quotaCounters) {
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
