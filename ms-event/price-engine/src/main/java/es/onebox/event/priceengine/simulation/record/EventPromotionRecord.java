/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.priceengine.simulation.record;

import java.util.Date;
import java.util.List;

/**
 * @author ignasi
 */
public class EventPromotionRecord {

    private Integer eventPromotionTemplateId;
    private Integer eventId;
    private Integer promotionTemplateId;
    private String name;
    private Integer status;
    private Integer subtype;
    private Boolean active;
    private Integer discountType;
    private Double fixedDiscountValue;
    private Double percentualDiscountValue;
    private Boolean applyChannelSpecificCharges;
    private Boolean applyPromoterSpecificCharges;
    private Boolean exclusiveSale;
    private Integer collectiveId;
    private Integer collectiveTypeId;
    private Integer collectiveSubtypeId;
    private String collectiveName;
    private String channels;
    private List<Long> sessions;
    private String priceZones;
    private String rates;
    private String ranges;
    private Date dateFrom;
    private Date dateTo;
    private Integer validationPeriodType;
    private Boolean selfManaged;
    private Boolean restrictiveAccess;
    private List<PromotionCommElemRecord> commElemRecords;
    private Boolean notCumulative;
    private Integer selectedChannels;
    private Integer selectedSessions;
    private Integer selectedRates;
    private Integer selectedPriceZones;

    private Boolean useEntityPacks;
    private Boolean useLimitByOperation;
    private Integer limitByOperation;
    private Boolean useLimitByEvent;
    private Integer limitByEvent;
    private Boolean useLimitBySession;
    private Integer limitBySession;
    private Boolean useLimitByMinTickets;
    private Integer limitByMinTickets;
    private Boolean useLimitByPack;
    private Integer limitByPack;
    private Boolean usesEventUserCollectiveLimit;
    private Integer eventUserCollectiveLimit;
    private Boolean usesSessionUserCollectiveLimit;
    private Integer sessionUserCollectiveLimit;
    private Boolean blockSecondaryMarketSale;

    public Integer getPromotionTemplateId() {
        return promotionTemplateId;
    }

    public void setPromotionTemplateId(Integer promotionTemplateId) {
        this.promotionTemplateId = promotionTemplateId;
    }

    public Integer getEventPromotionTemplateId() {
        return eventPromotionTemplateId;
    }

    public void setEventPromotionTemplateId(Integer eventPromotionTemplateId) {
        this.eventPromotionTemplateId = eventPromotionTemplateId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
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

    public String getChannels() {
        return channels;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String getPriceZones() {
        return priceZones;
    }

    public void setPriceZones(String priceZones) {
        this.priceZones = priceZones;
    }

    public String getRates() {
        return rates;
    }

    public void setRates(String rates) {
        this.rates = rates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRanges() {
        return ranges;
    }

    public void setRanges(String ranges) {
        this.ranges = ranges;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
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

    public Integer getCollectiveId() {
        return collectiveId;
    }

    public void setCollectiveId(Integer collectiveId) {
        this.collectiveId = collectiveId;
    }

    public Boolean getExclusiveSale() {
        return exclusiveSale;
    }

    public void setExclusiveSale(Boolean exclusiveSale) {
        this.exclusiveSale = exclusiveSale;
    }

    public Integer getCollectiveTypeId() {
        return collectiveTypeId;
    }

    public void setCollectiveTypeId(Integer collectiveTypeId) {
        this.collectiveTypeId = collectiveTypeId;
    }

    public List<Long> getSessions() {
        return sessions;
    }

    public void setSessions(List<Long> sessions) {
        this.sessions = sessions;
    }

    public Integer getCollectiveSubtypeId() {
        return collectiveSubtypeId;
    }

    public void setCollectiveSubtypeId(Integer collectiveSubtypeId) {
        this.collectiveSubtypeId = collectiveSubtypeId;
    }

    public String getCollectiveName() {
        return collectiveName;
    }

    public void setCollectiveName(String collectiveName) {
        this.collectiveName = collectiveName;
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

    public Integer getValidationPeriodType() {
        return validationPeriodType;
    }

    public void setValidationPeriodType(Integer validationPeriodType) {
        this.validationPeriodType = validationPeriodType;
    }

    public Boolean getSelfManaged() {
        return selfManaged;
    }

    public void setSelfManaged(Boolean selfManaged) {
        this.selfManaged = selfManaged;
    }

    public Boolean getRestrictiveAccess() {
        return restrictiveAccess;
    }

    public void setRestrictiveAccess(Boolean restrictiveAccess) {
        this.restrictiveAccess = restrictiveAccess;
    }

    public List<PromotionCommElemRecord> getCommElemRecords() {
        return commElemRecords;
    }

    public void setCommElemRecords(List<PromotionCommElemRecord> commElemRecords) {
        this.commElemRecords = commElemRecords;
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

    public Boolean getUseEntityPacks() {
        return useEntityPacks;
    }

    public void setUseEntityPacks(Boolean useEntityPacks) {
        this.useEntityPacks = useEntityPacks;
    }

    public Boolean getUseLimitByOperation() {
        return useLimitByOperation;
    }

    public void setUseLimitByOperation(Boolean useLimitByOperation) {
        this.useLimitByOperation = useLimitByOperation;
    }

    public Integer getLimitByOperation() {
        return limitByOperation;
    }

    public void setLimitByOperation(Integer limitByOperation) {
        this.limitByOperation = limitByOperation;
    }

    public Boolean getUseLimitByEvent() {
        return useLimitByEvent;
    }

    public void setUseLimitByEvent(Boolean useLimitByEvent) {
        this.useLimitByEvent = useLimitByEvent;
    }

    public Integer getLimitByEvent() {
        return limitByEvent;
    }

    public void setLimitByEvent(Integer limitByEvent) {
        this.limitByEvent = limitByEvent;
    }

    public Boolean getUseLimitBySession() {
        return useLimitBySession;
    }

    public void setUseLimitBySession(Boolean useLimitBySession) {
        this.useLimitBySession = useLimitBySession;
    }

    public Integer getLimitBySession() {
        return limitBySession;
    }

    public void setLimitBySession(Integer limitBySession) {
        this.limitBySession = limitBySession;
    }

    public Boolean getUseLimitByMinTickets() {
        return useLimitByMinTickets;
    }

    public void setUseLimitByMinTickets(Boolean useLimitByMinTickets) {
        this.useLimitByMinTickets = useLimitByMinTickets;
    }

    public Integer getLimitByMinTickets() {
        return limitByMinTickets;
    }

    public void setLimitByMinTickets(Integer limitByMinTickets) {
        this.limitByMinTickets = limitByMinTickets;
    }

    public Boolean getUseLimitByPack() {
        return useLimitByPack;
    }

    public void setUseLimitByPack(Boolean useLimitByPack) {
        this.useLimitByPack = useLimitByPack;
    }

    public Integer getLimitByPack() {
        return limitByPack;
    }

    public void setLimitByPack(Integer limitByPack) {
        this.limitByPack = limitByPack;
    }

    public Boolean getUsesEventUserCollectiveLimit() {
        return usesEventUserCollectiveLimit;
    }

    public void setUsesEventUserCollectiveLimit(Boolean usesEventUserCollectiveLimit) {
        this.usesEventUserCollectiveLimit = usesEventUserCollectiveLimit;
    }

    public Integer getEventUserCollectiveLimit() {
        return eventUserCollectiveLimit;
    }

    public void setEventUserCollectiveLimit(Integer eventUserCollectiveLimit) {
        this.eventUserCollectiveLimit = eventUserCollectiveLimit;
    }

    public Boolean getUsesSessionUserCollectiveLimit() {
        return usesSessionUserCollectiveLimit;
    }

    public void setUsesSessionUserCollectiveLimit(Boolean usesSessionUserCollectiveLimit) {
        this.usesSessionUserCollectiveLimit = usesSessionUserCollectiveLimit;
    }

    public Integer getSessionUserCollectiveLimit() {
        return sessionUserCollectiveLimit;
    }

    public void setSessionUserCollectiveLimit(Integer sessionUserCollectiveLimit) {
        this.sessionUserCollectiveLimit = sessionUserCollectiveLimit;
    }

    public Boolean getBlockSecondaryMarketSale() {
        return blockSecondaryMarketSale;
    }

    public void setBlockSecondaryMarketSale(Boolean blockSecondaryMarketSale) {
        this.blockSecondaryMarketSale = blockSecondaryMarketSale;
    }
}
