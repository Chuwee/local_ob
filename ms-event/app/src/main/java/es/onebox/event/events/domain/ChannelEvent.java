package es.onebox.event.events.domain;

import java.time.ZonedDateTime;

/**
 * Created by mmolinero on 19/02/19.
 */
public class ChannelEvent {

    private Integer channelEventId;
    private Integer channelId;
    private Integer eventId;
    private int relationState;
    private ZonedDateTime publishDate;
    private ZonedDateTime sellDate;
    private ZonedDateTime endDate;
    private Double comisionMax;
    private Double comisionMin;
    private Integer lockNumber;
    private Integer timeLock;
    private Boolean published;
    private Boolean isSelling;
    private Integer causeSellCancelation;
    private Integer causePublichCancelation;
    private Boolean isUsingEventDates;
    private Boolean allSellGroup;
    private Boolean allSellGroupB2BClient;
    private ZonedDateTime bookingIniDate;
    private ZonedDateTime bookingEndDate;
    private Boolean isBookingActive;
    private Boolean biMigrate;
    private Boolean includeNoTicketingProduct;
    private Boolean dirtyBi;
    private Boolean channelChargesSuggest;
    private Double maxCharges;
    private Double minCharges;
    private Boolean channelChargesInvitationSuggest;
    private Double maxChargesInvitation;
    private Double minChargesInvitation;
    private Boolean isUsingEventCharges;
    private Boolean isUsingEventChargePromotion;
    private Boolean chargesPromotionChannelSuggest;
    private Double maxChargePromotion;
    private Double minChargePromotion;

    public Integer getChannelEventId() {
        return channelEventId;
    }

    public void setChannelEventId(Integer channelEventId) {
        this.channelEventId = channelEventId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public int getRelationState() {
        return relationState;
    }

    public void setRelationState(int relationState) {
        this.relationState = relationState;
    }

    public ZonedDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(ZonedDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public ZonedDateTime getSellDate() {
        return sellDate;
    }

    public void setSellDate(ZonedDateTime sellDate) {
        this.sellDate = sellDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Double getComisionMax() {
        return comisionMax;
    }

    public void setComisionMax(Double comisionMax) {
        this.comisionMax = comisionMax;
    }

    public Double getComisionMin() {
        return comisionMin;
    }

    public void setComisionMin(Double comisionMin) {
        this.comisionMin = comisionMin;
    }

    public Integer getLockNumber() {
        return lockNumber;
    }

    public void setLockNumber(Integer lockNumber) {
        this.lockNumber = lockNumber;
    }

    public Integer getTimeLock() {
        return timeLock;
    }

    public void setTimeLock(Integer timeLock) {
        this.timeLock = timeLock;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getSelling() {
        return isSelling;
    }

    public void setSelling(Boolean selling) {
        isSelling = selling;
    }

    public Integer getCauseSellCancelation() {
        return causeSellCancelation;
    }

    public void setCauseSellCancelation(Integer causeSellCancelation) {
        this.causeSellCancelation = causeSellCancelation;
    }

    public Integer getCausePublichCancelation() {
        return causePublichCancelation;
    }

    public void setCausePublichCancelation(Integer causePublichCancelation) {
        this.causePublichCancelation = causePublichCancelation;
    }

    public Boolean getUsingEventDates() {
        return isUsingEventDates;
    }

    public void setUsingEventDates(Boolean usingEventDates) {
        isUsingEventDates = usingEventDates;
    }

    public Boolean getAllSellGroup() {
        return allSellGroup;
    }

    public void setAllSellGroup(Boolean allSellGroup) {
        this.allSellGroup = allSellGroup;
    }

    public Boolean getAllSellGroupB2BClient() {
        return allSellGroupB2BClient;
    }

    public void setAllSellGroupB2BClient(Boolean allSellGroupB2BClient) {
        this.allSellGroupB2BClient = allSellGroupB2BClient;
    }

    public ZonedDateTime getBookingIniDate() {
        return bookingIniDate;
    }

    public void setBookingIniDate(ZonedDateTime bookingIniDate) {
        this.bookingIniDate = bookingIniDate;
    }

    public ZonedDateTime getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(ZonedDateTime bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public Boolean getBookingActive() {
        return isBookingActive;
    }

    public void setBookingActive(Boolean bookingActive) {
        isBookingActive = bookingActive;
    }

    public Boolean getBiMigrate() {
        return biMigrate;
    }

    public void setBiMigrate(Boolean biMigrate) {
        this.biMigrate = biMigrate;
    }

    public Boolean getIncludeNoTicketingProduct() {
        return includeNoTicketingProduct;
    }

    public void setIncludeNoTicketingProduct(Boolean includeNoTicketingProduct) {
        this.includeNoTicketingProduct = includeNoTicketingProduct;
    }

    public Boolean getDirtyBi() {
        return dirtyBi;
    }

    public void setDirtyBi(Boolean dirtyBi) {
        this.dirtyBi = dirtyBi;
    }

    public Boolean getChannelChargesSuggest() {
        return channelChargesSuggest;
    }

    public void setChannelChargesSuggest(Boolean channelChargesSuggest) {
        this.channelChargesSuggest = channelChargesSuggest;
    }

    public Double getMaxCharges() {
        return maxCharges;
    }

    public void setMaxCharges(Double maxCharges) {
        this.maxCharges = maxCharges;
    }

    public Double getMinCharges() {
        return minCharges;
    }

    public void setMinCharges(Double minCharges) {
        this.minCharges = minCharges;
    }

    public Boolean getChannelChargesInvitationSuggest() {
        return channelChargesInvitationSuggest;
    }

    public void setChannelChargesInvitationSuggest(Boolean channelChargesInvitationSuggest) {
        this.channelChargesInvitationSuggest = channelChargesInvitationSuggest;
    }

    public Double getMaxChargesInvitation() {
        return maxChargesInvitation;
    }

    public void setMaxChargesInvitation(Double maxChargesInvitation) {
        this.maxChargesInvitation = maxChargesInvitation;
    }

    public Double getMinChargesInvitation() {
        return minChargesInvitation;
    }

    public void setMinChargesInvitation(Double minChargesInvitation) {
        this.minChargesInvitation = minChargesInvitation;
    }

    public Boolean getUsingEventCharges() {
        return isUsingEventCharges;
    }

    public void setUsingEventCharges(Boolean usingEventCharges) {
        isUsingEventCharges = usingEventCharges;
    }

    public Boolean getUsingEventChargePromotion() {
        return isUsingEventChargePromotion;
    }

    public void setUsingEventChargePromotion(Boolean usingEventChargePromotion) {
        isUsingEventChargePromotion = usingEventChargePromotion;
    }

    public Boolean getChargesPromotionChannelSuggest() {
        return chargesPromotionChannelSuggest;
    }

    public void setChargesPromotionChannelSuggest(Boolean chargesPromotionChannelSuggest) {
        this.chargesPromotionChannelSuggest = chargesPromotionChannelSuggest;
    }

    public Double getMaxChargePromotion() {
        return maxChargePromotion;
    }

    public void setMaxChargePromotion(Double maxChargePromotion) {
        this.maxChargePromotion = maxChargePromotion;
    }

    public Double getMinChargePromotion() {
        return minChargePromotion;
    }

    public void setMinChargePromotion(Double minChargePromotion) {
        this.minChargePromotion = minChargePromotion;
    }
}
