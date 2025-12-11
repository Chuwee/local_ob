package es.onebox.event.catalog.dao.record;


import es.onebox.jooq.cpanel.tables.records.CpanelSessionTaxesRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.sql.Timestamp;

public class SessionTaxesForCatalogRecord extends CpanelSessionTaxesRecord {

    private Long locationTaxId;
    private Long taxCounter;
    private Integer tipo;
    private String taxName;
    private Double minRange;
    private Double maxRange;
    private Double taxValue;
    private Boolean progressive;
    private Double minProgressive;
    private Double maxProgressive;
    private Timestamp startDate;
    private Timestamp endDate;
    private Integer capacityType; // 0 - total, 1 - percent
    private Integer capacityMin;
    private Integer capacityMax;

    public Long getLocationTaxId() {
        return locationTaxId;
    }

    public void setLocationTaxId(Long locationTaxId) {
        this.locationTaxId = locationTaxId;
    }

    public Long getTaxCounter() {
        return taxCounter;
    }

    public void setTaxCounter(Long taxCounter) {
        this.taxCounter = taxCounter;
    }

    @Override
    public Integer getTipo() {
        return tipo;
    }

    @Override
    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
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

    public Double getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(Double taxValue) {
        this.taxValue = taxValue;
    }

    public Boolean getProgressive() {
        return progressive;
    }

    public void setProgressive(Boolean progressive) {
        this.progressive = progressive;
    }

    public Double getMinProgressive() {
        return minProgressive;
    }

    public void setMinProgressive(Double minProgressive) {
        this.minProgressive = minProgressive;
    }

    public Double getMaxProgressive() {
        return maxProgressive;
    }

    public void setMaxProgressive(Double maxProgressive) {
        this.maxProgressive = maxProgressive;
    }

    public Timestamp getStartDate() { return startDate; }

    public void setStartDate(Timestamp startDate) { this.startDate = startDate; }

    public Timestamp getEndDate() { return endDate; }

    public void setEndDate(Timestamp endDate) { this.endDate = endDate; }

    public Integer getCapacityType() { return capacityType; }

    public void setCapacityType(Integer capacityType) { this.capacityType = capacityType; }

    public Integer getCapacityMin() { return capacityMin; }

    public void setCapacityMin(Integer capacityMin) { this.capacityMin = capacityMin; }

    public Integer getCapacityMax() { return capacityMax; }

    public void setCapacityMax(Integer capacityMax) { this.capacityMax = capacityMax; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
