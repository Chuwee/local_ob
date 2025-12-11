package es.onebox.mgmt.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ExternalConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 7808939200778344018L;

    private Long id;
    private SmartBookingConfig smartBooking;
    private SGAConfig sga;
    private ApimConfig apim;
    private List<Provider> inventoryProviders;
    private Boolean sectorsValidation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SmartBookingConfig getSmartBooking() {
        return smartBooking;
    }

    public void setSmartBooking(SmartBookingConfig smartBooking) {
        this.smartBooking = smartBooking;
    }

    public SGAConfig getSga() {
        return sga;
    }

    public void setSga(SGAConfig sga) {
        this.sga = sga;
    }

    public ApimConfig getApim() {
        return apim;
    }

    public void setApim(ApimConfig apim) {
        this.apim = apim;
    }

    public List<Provider> getInventoryProviders() {
        return inventoryProviders;
    }

    public void setInventoryProviders(List<Provider> inventoryProviders) {
        this.inventoryProviders = inventoryProviders;
    }

    public Boolean getSectorsValidation() {
        return sectorsValidation;
    }

    public void setSectorsValidation(Boolean sectorsValidation) {
        this.sectorsValidation = sectorsValidation;
    }

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
