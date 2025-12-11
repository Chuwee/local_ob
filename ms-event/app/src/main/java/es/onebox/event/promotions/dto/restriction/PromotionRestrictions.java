package es.onebox.event.promotions.dto.restriction;

import es.onebox.event.promotions.dto.PromotionCollective;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PromotionRestrictions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Long> channels;
    private List<Long> sessions;
    private List<Long> priceZones;
    private List<Long> rates;
    private PromotionValidationPeriod validationPeriod;
    private PromotionCollective collective;
    private RestrictionLimit packLimit;
    private RestrictionLimit eventLimit;
    private RestrictionLimit sessionLimit;
    private RestrictionLimit operationLimit;
    private RestrictionLimit minLimit;
    private RestrictionLimit eventCollectiveLimit;
    private RestrictionLimit sessionCollectiveLimit;
    private Boolean nonCummulative;

    public List<Long> getChannels() {
        return channels;
    }

    public void setChannels(List<Long> channels) {
        this.channels = channels;
    }

    public List<Long> getSessions() {
        return sessions;
    }

    public void setSessions(List<Long> sessions) {
        this.sessions = sessions;
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

    public PromotionValidationPeriod getValidationPeriod() {
        return validationPeriod;
    }

    public void setValidationPeriod(PromotionValidationPeriod validationPeriod) {
        this.validationPeriod = validationPeriod;
    }

    public PromotionCollective getCollective() {
        return collective;
    }

    public void setCollective(PromotionCollective collective) {
        this.collective = collective;
    }

    public RestrictionLimit getPackLimit() {
        return packLimit;
    }

    public void setPackLimit(RestrictionLimit packLimit) {
        this.packLimit = packLimit;
    }

    public RestrictionLimit getEventLimit() {
        return eventLimit;
    }

    public void setEventLimit(RestrictionLimit eventLimit) {
        this.eventLimit = eventLimit;
    }

    public RestrictionLimit getSessionLimit() {
        return sessionLimit;
    }

    public void setSessionLimit(RestrictionLimit sessionLimit) {
        this.sessionLimit = sessionLimit;
    }

    public RestrictionLimit getOperationLimit() {
        return operationLimit;
    }

    public void setOperationLimit(RestrictionLimit operationLimit) {
        this.operationLimit = operationLimit;
    }

    public RestrictionLimit getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(RestrictionLimit minLimit) {
        this.minLimit = minLimit;
    }

    public RestrictionLimit getEventCollectiveLimit() {
        return eventCollectiveLimit;
    }

    public void setEventCollectiveLimit(RestrictionLimit eventCollectiveLimit) {
        this.eventCollectiveLimit = eventCollectiveLimit;
    }

    public RestrictionLimit getSessionCollectiveLimit() {
        return sessionCollectiveLimit;
    }

    public void setSessionCollectiveLimit(RestrictionLimit sessionCollectiveLimit) {
        this.sessionCollectiveLimit = sessionCollectiveLimit;
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
