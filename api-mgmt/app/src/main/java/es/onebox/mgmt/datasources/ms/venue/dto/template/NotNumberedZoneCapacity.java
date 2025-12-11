package es.onebox.mgmt.datasources.ms.venue.dto.template;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class NotNumberedZoneCapacity extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer capacity;
    private Long sectorId;
    private Long viewId;
    private Integer statusValue;
    private Integer blockingReason;
    private Integer priceType;
    private Integer quota;
    private Integer visibilityValue;
    private Integer accessibilityValue;
    private Long gate;
    private List<QuotaCounter> quotaCounters;

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public Long getViewId() {
        return viewId;
    }

    public void setViewId(Long viewId) {
        this.viewId = viewId;
    }

    public Integer getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(Integer statusValue) {
        this.statusValue = statusValue;
    }

    public Integer getBlockingReason() {
        return blockingReason;
    }

    public void setBlockingReason(Integer blockingReason) {
        this.blockingReason = blockingReason;
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

    public Integer getVisibilityValue() {
        return visibilityValue;
    }

    public void setVisibilityValue(Integer visibilityValue) {
        this.visibilityValue = visibilityValue;
    }

    public Integer getAccessibilityValue() {
        return accessibilityValue;
    }

    public void setAccessibilityValue(Integer accessibilityValue) {
        this.accessibilityValue = accessibilityValue;
    }

    public Long getGate() {
        return gate;
    }

    public void setGate(Long gate) {
        this.gate = gate;
    }

    public List<QuotaCounter> getQuotaCounters() {
        return quotaCounters;
    }

    public void setQuotaCounters(List<QuotaCounter> quotaCounters) {
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
