package es.onebox.event.priceengine.simulation.domain;

import java.util.List;

public class Promotion {

        private Integer id;
        private String name;
        private Integer status;
        private Integer subtype;
        private Boolean active;
        private Integer discountType;
        private Double fixedDiscountValue;
        private Double percentualDiscountValue;
        private Boolean applyChannelSpecificCharges;
        private Boolean applyPromoterSpecificCharges;
        private List<Long> sessions;
        private List<Integer> priceZones;
        private List<Integer> rates;
        private List<PromotionPricesRange> ranges;
        private Boolean notCumulative;
        private Integer selectedChannels;
        private Integer selectedSessions;
        private Integer selectedRates;
        private Integer selectedPriceZones;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSubtype() {
        return subtype;
    }

    public void setSubtype(Integer subtype) {
        this.subtype = subtype;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getDiscountType() {
        return discountType;
    }

    public void setDiscountType(Integer discountType) {
        this.discountType = discountType;
    }

    public Double getFixedDiscountValue() {
        return fixedDiscountValue;
    }

    public void setFixedDiscountValue(Double fixedDiscountValue) {
        this.fixedDiscountValue = fixedDiscountValue;
    }

    public Double getPercentualDiscountValue() {
        return percentualDiscountValue;
    }

    public void setPercentualDiscountValue(Double percentualDiscountValue) {
        this.percentualDiscountValue = percentualDiscountValue;
    }

    public Boolean getApplyChannelSpecificCharges() {
        return applyChannelSpecificCharges;
    }

    public void setApplyChannelSpecificCharges(Boolean applyChannelSpecificCharges) {
        this.applyChannelSpecificCharges = applyChannelSpecificCharges;
    }

    public Boolean getApplyPromoterSpecificCharges() {
        return applyPromoterSpecificCharges;
    }

    public void setApplyPromoterSpecificCharges(Boolean applyPromoterSpecificCharges) {
        this.applyPromoterSpecificCharges = applyPromoterSpecificCharges;
    }

    public List<Long> getSessions() {
        return sessions;
    }

    public void setSessions(List<Long> sessions) {
        this.sessions = sessions;
    }

    public List<Integer> getPriceZones() {
        return priceZones;
    }

    public void setPriceZones(List<Integer> priceZones) {
        this.priceZones = priceZones;
    }

    public List<Integer> getRates() {
        return rates;
    }

    public void setRates(List<Integer> rates) {
        this.rates = rates;
    }

    public List<PromotionPricesRange> getRanges() {
        return ranges;
    }

    public void setRanges(List<PromotionPricesRange> ranges) {
        this.ranges = ranges;
    }

    public Boolean getNotCumulative() {
        return notCumulative;
    }

    public void setNotCumulative(Boolean notCumulative) {
        this.notCumulative = notCumulative;
    }

    public Integer getSelectedChannels() {
        return selectedChannels;
    }

    public void setSelectedChannels(Integer selectedChannels) {
        this.selectedChannels = selectedChannels;
    }

    public Integer getSelectedSessions() {
        return selectedSessions;
    }

    public void setSelectedSessions(Integer selectedSessions) {
        this.selectedSessions = selectedSessions;
    }

    public Integer getSelectedRates() {
        return selectedRates;
    }

    public void setSelectedRates(Integer selectedRates) {
        this.selectedRates = selectedRates;
    }

    public Integer getSelectedPriceZones() {
        return selectedPriceZones;
    }

    public void setSelectedPriceZones(Integer selectedPriceZones) {
        this.selectedPriceZones = selectedPriceZones;
    }
}
