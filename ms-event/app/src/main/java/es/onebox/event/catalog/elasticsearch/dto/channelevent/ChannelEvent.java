package es.onebox.event.catalog.elasticsearch.dto.channelevent;

import es.onebox.couchbase.annotations.Id;
import es.onebox.event.events.domain.eventconfig.EventChangeSeatConfig;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChannelEvent implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;

    @Id(index = 1)
    private Long channelId;
    @Id(index = 2)
    private Long eventId;
    private String channelName;
    private Long channelEventId;
    private Long channelEntityId;
    private Integer channelEventStatus;
    private Date publishChannelEventDate;
    private Date purchaseChannelEventDate;
    private Boolean publishChannelEvent;
    private Boolean purchaseChannelEvent;
    private Boolean purchaseSecondaryMarketChannelEvent;
    private Date endChannelEventDate;
    private Boolean eventDates;
    private Date beginBookingChannelEventDate;
    private Date endBookingChannelEventDate;
    private Boolean enabledBookingChannelEvent;
    private Integer customCategoryId;
    private String customCategoryName;
    private String customCategoryCode;
    private Integer customParentCategoryId;
    private String customParentCategoryName;
    private String customParentCategoryCode;
    private Boolean multiVenue;
    private Boolean multiLocation;
    private List<Long> venueIds;
    private ChannelEventSurcharges surcharges;
    private List<ChannelEventCommunicationElement> communicationElements;
    private ChannelCatalogEventInfo catalogInfo;
    private Boolean allowChannelPromotions;
    private Boolean sessionsShowDate;
    private Boolean sessionsShowDateTime;
    private Boolean sessionsShowSchedule;
    private Boolean sessionsNoFinalDate;
    private Boolean ticketHandling;
    private Boolean allowChannelUseAlternativeCharges;
    private Boolean hasSessions;
    private Boolean hasSessionPacks;
    private ChannelEventPostBookingQuestions postBookingQuestions;
    private EventChangeSeatConfig eventChangeSeatConfig;
    private ZonedDateTime firstPublishedSession;
    private ZonedDateTime firstPublishedSessionPack;
    private Map<String, String> infoBannerSaleRequest;
    private Boolean phoneValidationRequired;
    private Boolean attendantVerificationRequired;
    private ChannelSubtype channelSubtype;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getChannelEventId() {
        return channelEventId;
    }

    public void setChannelEventId(Long channelEventId) {
        this.channelEventId = channelEventId;
    }

    public Long getChannelEntityId() {
        return channelEntityId;
    }

    public void setChannelEntityId(Long channelEntityId) {
        this.channelEntityId = channelEntityId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getChannelEventStatus() {
        return channelEventStatus;
    }

    public void setChannelEventStatus(Integer channelEventStatus) {
        this.channelEventStatus = channelEventStatus;
    }

    public Date getPublishChannelEventDate() {
        return publishChannelEventDate;
    }

    public void setPublishChannelEventDate(Date publishChannelEventDate) {
        this.publishChannelEventDate = publishChannelEventDate;
    }

    public Date getPurchaseChannelEventDate() {
        return purchaseChannelEventDate;
    }

    public void setPurchaseChannelEventDate(Date purchaseChannelEventDate) {
        this.purchaseChannelEventDate = purchaseChannelEventDate;
    }

    public Boolean getPublishChannelEvent() {
        return publishChannelEvent;
    }

    public void setPublishChannelEvent(Boolean publishChannelEvent) {
        this.publishChannelEvent = publishChannelEvent;
    }

    public Boolean getPurchaseChannelEvent() {
        return purchaseChannelEvent;
    }

    public void setPurchaseChannelEvent(Boolean purchaseChannelEvent) {
        this.purchaseChannelEvent = purchaseChannelEvent;
    }

    public Boolean getPurchaseSecondaryMarketChannelEvent() {
        return purchaseSecondaryMarketChannelEvent;
    }

    public void setPurchaseSecondaryMarketChannelEvent(Boolean purchaseSecondaryMarketChannelEvent) {
        this.purchaseSecondaryMarketChannelEvent = purchaseSecondaryMarketChannelEvent;
    }

    public Date getEndChannelEventDate() {
        return endChannelEventDate;
    }

    public void setEndChannelEventDate(Date endChannelEventDate) {
        this.endChannelEventDate = endChannelEventDate;
    }

    public Boolean getEventDates() {
        return eventDates;
    }

    public void setEventDates(Boolean eventDates) {
        this.eventDates = eventDates;
    }

    public Date getBeginBookingChannelEventDate() {
        return beginBookingChannelEventDate;
    }

    public void setBeginBookingChannelEventDate(Date beginBookingChannelEventDate) {
        this.beginBookingChannelEventDate = beginBookingChannelEventDate;
    }

    public Date getEndBookingChannelEventDate() {
        return endBookingChannelEventDate;
    }

    public void setEndBookingChannelEventDate(Date endBookingChannelEventDate) {
        this.endBookingChannelEventDate = endBookingChannelEventDate;
    }

    public Boolean getEnabledBookingChannelEvent() {
        return enabledBookingChannelEvent;
    }

    public void setEnabledBookingChannelEvent(Boolean enabledBookingChannelEvent) {
        this.enabledBookingChannelEvent = enabledBookingChannelEvent;
    }

    public Integer getCustomCategoryId() {
        return customCategoryId;
    }

    public void setCustomCategoryId(Integer customCategoryId) {
        this.customCategoryId = customCategoryId;
    }

    public String getCustomCategoryName() {
        return customCategoryName;
    }

    public void setCustomCategoryName(String customCategoryName) {
        this.customCategoryName = customCategoryName;
    }

    public String getCustomCategoryCode() {
        return customCategoryCode;
    }

    public void setCustomCategoryCode(String customCategoryCode) {
        this.customCategoryCode = customCategoryCode;
    }

    public Integer getCustomParentCategoryId() {
        return customParentCategoryId;
    }

    public void setCustomParentCategoryId(Integer customParentCategoryId) {
        this.customParentCategoryId = customParentCategoryId;
    }

    public String getCustomParentCategoryName() {
        return customParentCategoryName;
    }

    public void setCustomParentCategoryName(String customParentCategoryName) {
        this.customParentCategoryName = customParentCategoryName;
    }

    public String getCustomParentCategoryCode() {
        return customParentCategoryCode;
    }

    public void setCustomParentCategoryCode(String customParentCategoryCode) {
        this.customParentCategoryCode = customParentCategoryCode;
    }

    public Boolean getMultiVenue() {
        return multiVenue;
    }

    public void setMultiVenue(Boolean multiVenue) {
        this.multiVenue = multiVenue;
    }

    public Boolean getMultiLocation() {
        return multiLocation;
    }

    public void setMultiLocation(Boolean multiLocation) {
        this.multiLocation = multiLocation;
    }

    public List<Long> getVenueIds() {
        return venueIds;
    }

    public void setVenueIds(List<Long> venueIds) {
        this.venueIds = venueIds;
    }

    public ChannelEventSurcharges getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(ChannelEventSurcharges surcharges) {
        this.surcharges = surcharges;
    }

    public List<ChannelEventCommunicationElement> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<ChannelEventCommunicationElement> communicationElements) {
        this.communicationElements = communicationElements;
    }

    public ChannelCatalogEventInfo getCatalogInfo() {
        return catalogInfo;
    }

    public void setCatalogInfo(ChannelCatalogEventInfo catalogInfo) {
        this.catalogInfo = catalogInfo;
    }

    public Boolean getAllowChannelPromotions() {
        return allowChannelPromotions;
    }

    public void setAllowChannelPromotions(Boolean allowChannelPromotions) {
        this.allowChannelPromotions = allowChannelPromotions;
    }

    public Boolean getSessionsShowDate() {
        return sessionsShowDate;
    }

    public ChannelEvent setSessionsShowDate(Boolean sessionsShowDate) {
        this.sessionsShowDate = sessionsShowDate;
        return this;
    }

    public Boolean getSessionsShowDateTime() {
        return sessionsShowDateTime;
    }

    public ChannelEvent setSessionsShowDateTime(Boolean sessionsShowDateTime) {
        this.sessionsShowDateTime = sessionsShowDateTime;
        return this;
    }

    public Boolean getSessionsShowSchedule() {
        return sessionsShowSchedule;
    }

    public ChannelEvent setSessionsShowSchedule(Boolean sessionsShowSchedule) {
        this.sessionsShowSchedule = sessionsShowSchedule;
        return this;
    }

    public Boolean getSessionsNoFinalDate() {
        return sessionsNoFinalDate;
    }

    public ChannelEvent setSessionsNoFinalDate(Boolean sessionsNoFinalDate) {
        this.sessionsNoFinalDate = sessionsNoFinalDate;
        return this;
    }

    public Boolean getTicketHandling() {
        return ticketHandling;
    }

    public void setTicketHandling(Boolean ticketHandling) {
        this.ticketHandling = ticketHandling;
    }

    public Boolean getAllowChannelUseAlternativeCharges() {
        return allowChannelUseAlternativeCharges;
    }

    public void setAllowChannelUseAlternativeCharges(Boolean allowChannelUseAlternativeCharges) {
        this.allowChannelUseAlternativeCharges = allowChannelUseAlternativeCharges;
    }

    public Boolean getHasSessions() {
        return hasSessions;
    }

    public void setHasSessions(Boolean hasSessions) {
        this.hasSessions = hasSessions;
    }

    public Boolean getHasSessionPacks() {
        return hasSessionPacks;
    }

    public void setHasSessionPacks(Boolean hasSessionPacks) {
        this.hasSessionPacks = hasSessionPacks;
    }

    public ChannelEventPostBookingQuestions getPostBookingQuestions() {
        return postBookingQuestions;
    }

    public void setPostBookingQuestions(ChannelEventPostBookingQuestions postBookingQuestions) {
        this.postBookingQuestions = postBookingQuestions;
    }

    public EventChangeSeatConfig getEventChangeSeatConfig() {
        return eventChangeSeatConfig;
    }

    public void setEventChangeSeatConfig(EventChangeSeatConfig eventChangeSeatConfig) {
        this.eventChangeSeatConfig = eventChangeSeatConfig;
    }

    public Map<String, String> getInfoBannerSaleRequest() {
        return infoBannerSaleRequest;
    }

    public void setInfoBannerSaleRequest(Map<String, String> infoBannerSaleRequest) {
        this.infoBannerSaleRequest = infoBannerSaleRequest;
    }

    public ZonedDateTime getFirstPublishedSessionPack() {
        return firstPublishedSessionPack;
    }

    public void setFirstPublishedSessionPack(ZonedDateTime firstPublishedSessionPack) {
        this.firstPublishedSessionPack = firstPublishedSessionPack;
    }

    public ZonedDateTime getFirstPublishedSession() {
        return firstPublishedSession;
    }

    public void setFirstPublishedSession(ZonedDateTime firstPublishedSession) {
        this.firstPublishedSession = firstPublishedSession;
    }

    public Boolean getPhoneValidationRequired() {
        return phoneValidationRequired;
    }

    public void setPhoneValidationRequired(Boolean phoneValidationRequired) {
        this.phoneValidationRequired = phoneValidationRequired;
    }

    public Boolean getAttendantVerificationRequired() {
        return attendantVerificationRequired;
    }

    public void setAttendantVerificationRequired(Boolean attendantVerificationRequired) {
        this.attendantVerificationRequired = attendantVerificationRequired;
    }

    public ChannelSubtype getChannelSubtype() {
        return channelSubtype;
    }

    public void setChannelSubtype(ChannelSubtype channelSubtype) {
        this.channelSubtype = channelSubtype;
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
