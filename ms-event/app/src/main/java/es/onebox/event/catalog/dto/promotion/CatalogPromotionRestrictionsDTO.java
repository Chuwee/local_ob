package es.onebox.event.catalog.dto.promotion;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class CatalogPromotionRestrictionsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private CatalogPromotionValidationPeriodDTO validationPeriod;

    private RestrictionLimitDTO packLimit;
    private RestrictionLimitDTO eventLimit;
    private RestrictionLimitDTO sessionLimit;
    private RestrictionLimitDTO operationLimit;
    private RestrictionLimitDTO minLimit;
    private RestrictionLimitDTO eventCollectiveLimit;
    private RestrictionLimitDTO sessionCollectiveLimit;
    private List<Long> priceZones;
    private List<Long> rates;
    private Boolean nonCummulative;

    public CatalogPromotionValidationPeriodDTO getValidationPeriod() {
        return validationPeriod;
    }

    public void setValidationPeriod(CatalogPromotionValidationPeriodDTO validationPeriod) {
        this.validationPeriod = validationPeriod;
    }

    public RestrictionLimitDTO getPackLimit() {
        return packLimit;
    }

    public void setPackLimit(RestrictionLimitDTO packLimit) {
        this.packLimit = packLimit;
    }

    public RestrictionLimitDTO getEventLimit() {
        return eventLimit;
    }

    public void setEventLimit(RestrictionLimitDTO eventLimit) {
        this.eventLimit = eventLimit;
    }

    public RestrictionLimitDTO getSessionLimit() {
        return sessionLimit;
    }

    public void setSessionLimit(RestrictionLimitDTO sessionLimit) {
        this.sessionLimit = sessionLimit;
    }

    public RestrictionLimitDTO getOperationLimit() {
        return operationLimit;
    }

    public void setOperationLimit(RestrictionLimitDTO operationLimit) {
        this.operationLimit = operationLimit;
    }

    public RestrictionLimitDTO getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(RestrictionLimitDTO minLimit) {
        this.minLimit = minLimit;
    }

    public RestrictionLimitDTO getEventCollectiveLimit() {
        return eventCollectiveLimit;
    }

    public void setEventCollectiveLimit(RestrictionLimitDTO eventCollectiveLimit) {
        this.eventCollectiveLimit = eventCollectiveLimit;
    }

    public RestrictionLimitDTO getSessionCollectiveLimit() {
        return sessionCollectiveLimit;
    }

    public void setSessionCollectiveLimit(RestrictionLimitDTO sessionCollectiveLimit) {
        this.sessionCollectiveLimit = sessionCollectiveLimit;
    }

    public List<Long> getPriceZones() {
        return priceZones;
    }

    public void setPriceZones(List<Long> priceZones) {
        this.priceZones = priceZones;
    }

    public List<Long> getRates() {
        return rates;
    }

    public void setRates(List<Long> rates) {
        this.rates = rates;
    }

    public Boolean getNonCummulative() {
        return nonCummulative;
    }

    public void setNonCummulative(Boolean nonCummulative) {
        this.nonCummulative = nonCummulative;
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
