package es.onebox.event.priceengine.taxes.domain;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.time.LocalDateTime;

public abstract class TaxInfo extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    private Double minRange;
    private Double maxRange;
    private Double value;
    private Boolean progressive;
    private Double progressiveMin;
    private Double progressiveMax;
    private String description;
    private CapacityRangeType capacityTypeId;
    private Integer capacityMin;
    private Integer capacityMax;
    private LocalDateTime startDate;
    private LocalDateTime endDate;


    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMinRange() {
        return minRange;
    }

    public void setMinRange(Double minRange) {
        this.minRange = minRange;
    }

    public Double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(Double maxRange) {
        this.maxRange = maxRange;
    }

    public Boolean getProgressive() {
        return progressive;
    }

    public void setProgressive(Boolean progressive) {
        this.progressive = progressive;
    }

    public Double getProgressiveMin() {
        return progressiveMin;
    }

    public void setProgressiveMin(Double progressiveMin) {
        this.progressiveMin = progressiveMin;
    }

    public Double getProgressiveMax() {
        return progressiveMax;
    }

    public void setProgressiveMax(Double progressiveMax) {
        this.progressiveMax = progressiveMax;
    }

    public CapacityRangeType getCapacityTypeId() { return capacityTypeId; }

    public void setCapacityTypeId(CapacityRangeType capacityTypeId) { this.capacityTypeId = capacityTypeId; }

    public Integer getCapacityMin() { return capacityMin; }

    public void setCapacityMin(Integer capacityMin) { this.capacityMin = capacityMin; }

    public Integer getCapacityMax() { return capacityMax; }

    public void setCapacityMax(Integer capacityMax) { this.capacityMax = capacityMax; }

    public LocalDateTime getStartDate() { return startDate; }

    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }

    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
