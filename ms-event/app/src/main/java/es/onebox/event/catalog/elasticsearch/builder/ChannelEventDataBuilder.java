package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.event.catalog.elasticsearch.dto.JoinField;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventPostBookingQuestions;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.events.domain.eventconfig.EventChangeSeatConfig;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static es.onebox.event.catalog.elasticsearch.utils.EventDataUtils.KEY_CHANNEL_EVENT;

public class ChannelEventDataBuilder extends ChannelEventBaseBuilder {

    private ChannelEventDataBuilder() {
        super();
    }

    public static ChannelEventDataBuilder builder() {
        return new ChannelEventDataBuilder();
    }

    public ChannelEventDataBuilder channelId(final Long value) {
        this.channelId = value;
        return this;
    }

    public ChannelEventDataBuilder eventId(final Long value) {
        this.eventId = value;
        return this;
    }

    public ChannelEventDataBuilder channelEventId(final Long value) {
        this.channelEventId = value;
        return this;
    }

    public ChannelEventDataBuilder channelEntityId(final Long value) {
        this.channelEntityId = value;
        return this;
    }

    public ChannelEventDataBuilder channelName(final String value) {
        this.channelName = value;
        return this;
    }

    public ChannelEventDataBuilder channelEventStatus(final Integer value) {
        this.channelEventStatus = value;
        return this;
    }

    public ChannelEventDataBuilder publishChannelEventDate(final Date value) {
        this.publishChannelEventDate = value;
        return this;
    }

    public ChannelEventDataBuilder purchaseChannelEventDate(final Date value) {
        this.purchaseChannelEventDate = value;
        return this;
    }

    public ChannelEventDataBuilder publishChannelEvent(final Boolean value) {
        this.publishChannelEvent = value;
        return this;
    }

    public ChannelEventDataBuilder purchaseChannelEvent(final Boolean value) {
        this.purchaseChannelEvent = value;
        return this;
    }

    public ChannelEventDataBuilder purchaseSecondaryMarketChannelEvent(final Boolean value) {
        this.purchaseSecondaryMarketChannelEvent = value;
        return this;
    }

    public ChannelEventDataBuilder endChannelEventDate(final Date value) {
        this.endChannelEventDate = value;
        return this;
    }

    public ChannelEventDataBuilder eventDates(final Boolean value) {
        this.eventDates = value;
        return this;
    }

    public ChannelEventDataBuilder beginBookingChannelEventDate(final Date value) {
        this.beginBookingChannelEventDate = value;
        return this;
    }

    public ChannelEventDataBuilder endBookingChannelEventDate(final Date value) {
        this.endBookingChannelEventDate = value;
        return this;
    }

    public ChannelEventDataBuilder enabledBookingChannelEvent(final Boolean value) {
        this.enabledBookingChannelEvent = value;
        return this;
    }

    public ChannelEventDataBuilder customCategoryId(final Integer value) {
        this.customCategoryId = value;
        return this;
    }

    public ChannelEventDataBuilder customCategoryName(final String value) {
        this.customCategoryName = value;
        return this;
    }

    public ChannelEventDataBuilder customCategoryCode(final String value) {
        this.customCategoryCode = value;
        return this;
    }

    public ChannelEventDataBuilder customParentCategoryId(final Integer value) {
        this.customParentCategoryId = value;
        return this;
    }

    public ChannelEventDataBuilder customParentCategoryName(final String value) {
        this.customParentCategoryName = value;
        return this;
    }

    public ChannelEventDataBuilder customParentCategoryCode(final String value) {
        this.customParentCategoryCode = value;
        return this;
    }

    public ChannelEventDataBuilder surcharges(final ChannelEventSurcharges value) {
        this.surcharges = value;
        return this;
    }

    public ChannelEventDataBuilder venueIds(List<Long> venueIds) {
        this.venueIds = venueIds;
        return this;
    }

    public ChannelEventDataBuilder multiVenue(Boolean multiVenue) {
        this.multiVenue = multiVenue;
        return this;
    }

    public ChannelEventDataBuilder multiLocation(Boolean multiLocation) {
        this.multiLocation = multiLocation;
        return this;
    }

    public ChannelEventDataBuilder catalogInfo(ChannelCatalogEventInfo catalogInfo) {
        this.catalogInfo = catalogInfo;
        return this;
    }

    public ChannelEventDataBuilder allowChannelPromotions(Boolean allowChannelPromotions) {
        this.allowChannelPromotions = allowChannelPromotions;
        return this;
    }

    public ChannelEventDataBuilder sessionsShowDate(final Boolean sessionsShowDate) {
        this.sessionsShowDate = sessionsShowDate;
        return this;
    }

    public ChannelEventDataBuilder sessionsShowDateTime(final Boolean sessionsShowDateTime) {
        this.sessionsShowDateTime = sessionsShowDateTime;
        return this;
    }

    public ChannelEventDataBuilder sessionsShowSchedule(final Boolean sessionsShowSchedule) {
        this.sessionsShowSchedule = sessionsShowSchedule;
        return this;
    }

    public ChannelEventDataBuilder sessionsNoFinalDate(final Boolean sessionsNoFinalDate) {
        this.sessionsNoFinalDate = sessionsNoFinalDate;
        return this;
    }

    public ChannelEventDataBuilder ticketHandling(final Boolean ticketHandling) {
        this.ticketHandling = ticketHandling;
        return this;
    }

    public ChannelEventDataBuilder useAlternativePromoterSurcharges(final Boolean useAlternativeSurcharges) {
        this.useAlternativePromoterSurcharges = useAlternativeSurcharges;
        return this;
    }

    public ChannelEventDataBuilder hasSessions(final Boolean hasSessions) {
        this.hasSessions = hasSessions;
        return this;
    }

    public ChannelEventDataBuilder hasSessionPacks(final Boolean hasSessionPacks) {
        this.hasSessionPacks = hasSessionPacks;
        return this;
    }

    public ChannelEventDataBuilder postBookingQuestions(final ChannelEventPostBookingQuestions postBookingQuestions) {
        this.postBookingQuestions = postBookingQuestions;
        return this;
    }

    public ChannelEventDataBuilder eventChangeSeatConfig(final EventChangeSeatConfig eventChangeSeatConfig){
        this.eventChangeSeatConfig = eventChangeSeatConfig;
        return this;
    }

    public ChannelEventDataBuilder infoBannerSaleRequest(final Map<String, String> infoBannerSaleRequest) {
        this.infoBannerSaleRequest = infoBannerSaleRequest;
        return this;
    }

    public ChannelEventDataBuilder mustBeIndexed(Boolean mustBeIndexed) {
        this.mustBeIndexed = mustBeIndexed;
        return this;
    }

    public ChannelEventDataBuilder firstPublishedSession(final Optional<Timestamp> firstPublishedSession) {
        firstPublishedSession.ifPresent(timestamp -> this.firstPublishedDateSession = timestamp.toLocalDateTime().atZone(ZoneOffset.UTC));
        return this;
    }

    public ChannelEventDataBuilder firstPublishedSessionPack(final Optional<Timestamp> firstPublishedSessionPack) {
        firstPublishedSessionPack.ifPresent(timestamp -> this.firstPublishedDateSessionPack = timestamp.toLocalDateTime().atZone(ZoneOffset.UTC));
        return this;
    }

    public ChannelEventDataBuilder phoneValidationRequired(Boolean phoneValidationRequired) {
        this.phoneValidationRequired = phoneValidationRequired;
        return this;
    }

    public ChannelEventDataBuilder attendantVerificationRequired(Boolean attendantVerificationRequired) {
        this.attendantVerificationRequired = attendantVerificationRequired;
        return this;
    }

    public ChannelEventDataBuilder channelSubtype(ChannelSubtype channelSubtype) {
        this.channelSubtype = channelSubtype;
        return this;
    }

    public ChannelEventData build() {
        ChannelEventData channelEventData = buildChannelEventData(channelId, eventId);
        channelEventData.setMustBeIndexed(mustBeIndexed);

        ChannelEvent channelEvent = new ChannelEvent();
        channelEvent.setChannelId(channelId);
        channelEvent.setChannelEventId(channelEventId);
        channelEvent.setChannelEntityId(channelEntityId);
        channelEvent.setEventId(eventId);
        channelEvent.setChannelName(channelName);
        channelEvent.setChannelEventStatus(channelEventStatus);
        channelEvent.setPublishChannelEventDate(publishChannelEventDate);
        channelEvent.setPurchaseChannelEventDate(purchaseChannelEventDate);
        channelEvent.setPublishChannelEvent(publishChannelEvent);
        channelEvent.setPurchaseChannelEvent(purchaseChannelEvent);
        channelEvent.setPurchaseSecondaryMarketChannelEvent(purchaseSecondaryMarketChannelEvent);
        channelEvent.setEndChannelEventDate(endChannelEventDate);
        channelEvent.setEventDates(eventDates);
        channelEvent.setEnabledBookingChannelEvent(enabledBookingChannelEvent);
        channelEvent.setBeginBookingChannelEventDate(beginBookingChannelEventDate);
        channelEvent.setEndBookingChannelEventDate(endBookingChannelEventDate);
        channelEvent.setCustomCategoryId(customCategoryId);
        channelEvent.setCustomCategoryCode(customCategoryCode);
        channelEvent.setCustomCategoryName(customCategoryName);
        channelEvent.setCustomParentCategoryId(customParentCategoryId);
        channelEvent.setCustomParentCategoryCode(customParentCategoryCode);
        channelEvent.setCustomParentCategoryName(customParentCategoryName);
        channelEvent.setSurcharges(surcharges);
        channelEvent.setVenueIds(venueIds);
        channelEvent.setMultiVenue(multiVenue);
        channelEvent.setMultiLocation(multiLocation);
        channelEvent.setCatalogInfo(catalogInfo);
        channelEvent.setAllowChannelPromotions(allowChannelPromotions);
        channelEvent.setSessionsShowDate(sessionsShowDate);
        channelEvent.setSessionsShowDateTime(sessionsShowDateTime);
        channelEvent.setSessionsShowSchedule(sessionsShowSchedule);
        channelEvent.setSessionsNoFinalDate(sessionsNoFinalDate);
        channelEvent.setTicketHandling(ticketHandling);
        channelEvent.setAllowChannelUseAlternativeCharges(useAlternativePromoterSurcharges);
        channelEvent.setHasSessions(hasSessions);
        channelEvent.setHasSessionPacks(hasSessionPacks);
        channelEvent.setPostBookingQuestions(postBookingQuestions);
        channelEvent.setEventChangeSeatConfig(eventChangeSeatConfig);
        channelEvent.setFirstPublishedSession(firstPublishedDateSession);
        channelEvent.setFirstPublishedSessionPack(firstPublishedDateSessionPack);
        channelEvent.setInfoBannerSaleRequest(infoBannerSaleRequest);
        channelEvent.setPhoneValidationRequired(phoneValidationRequired);
        channelEvent.setAttendantVerificationRequired(attendantVerificationRequired);
        channelEvent.setChannelSubtype(channelSubtype);
        channelEventData.setChannelEvent(channelEvent);
        return channelEventData;
    }

    public static ChannelEventData buildChannelEventData(Long channelId, Long eventId) {
        ChannelEventData channelEventData = new ChannelEventData();
        channelEventData.setId(EventDataUtils.getChannelEventKey(channelId, eventId));
        channelEventData.setJoin(new JoinField(KEY_CHANNEL_EVENT, EventDataUtils.getEventKey(eventId)));
        return channelEventData;
    }

}
