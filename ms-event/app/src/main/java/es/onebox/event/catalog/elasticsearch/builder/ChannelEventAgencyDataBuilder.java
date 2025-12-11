package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.event.catalog.elasticsearch.dto.JoinField;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgencyData;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.events.dto.conditions.ProfessionalClientConditions;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static es.onebox.event.catalog.elasticsearch.utils.EventDataUtils.KEY_CHANNEL_EVENT_AGENCY;

public class ChannelEventAgencyDataBuilder extends ChannelEventBaseBuilder {

    private Long agencyId;
    private ProfessionalClientConditions agencyConditions;

    private ChannelEventAgencyDataBuilder() {
        super();
    }

    public static ChannelEventAgencyDataBuilder builder() {
        return new ChannelEventAgencyDataBuilder();
    }


    public ChannelEventAgencyDataBuilder agencyConditions(final ProfessionalClientConditions value) {
        this.agencyConditions = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder agencyId(final Long value) {
        this.agencyId = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder channelId(final Long value) {
        this.channelId = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder eventId(final Long value) {
        this.eventId = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder channelEventId(final Long value) {
        this.channelEventId = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder channelEntityId(final Long value) {
        this.channelEntityId = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder channelName(final String value) {
        this.channelName = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder channelEventStatus(final Integer value) {
        this.channelEventStatus = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder publishChannelEventDate(final Date value) {
        this.publishChannelEventDate = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder purchaseChannelEventDate(final Date value) {
        this.purchaseChannelEventDate = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder publishChannelEvent(final Boolean value) {
        this.publishChannelEvent = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder purchaseChannelEvent(final Boolean value) {
        this.purchaseChannelEvent = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder purchaseSecondaryMarketChannelEvent(final Boolean value) {
        this.purchaseSecondaryMarketChannelEvent = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder endChannelEventDate(final Date value) {
        this.endChannelEventDate = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder eventDates(final Boolean value) {
        this.eventDates = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder beginBookingChannelEventDate(final Date value) {
        this.beginBookingChannelEventDate = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder endBookingChannelEventDate(final Date value) {
        this.endBookingChannelEventDate = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder enabledBookingChannelEvent(final Boolean value) {
        this.enabledBookingChannelEvent = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder customCategoryId(final Integer value) {
        this.customCategoryId = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder customCategoryName(final String value) {
        this.customCategoryName = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder customCategoryCode(final String value) {
        this.customCategoryCode = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder customParentCategoryId(final Integer value) {
        this.customParentCategoryId = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder customParentCategoryName(final String value) {
        this.customParentCategoryName = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder customParentCategoryCode(final String value) {
        this.customParentCategoryCode = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder surcharges(final ChannelEventSurcharges value) {
        this.surcharges = value;
        return this;
    }

    public ChannelEventAgencyDataBuilder venueIds(List<Long> venueIds) {
        this.venueIds = venueIds;
        return this;
    }

    public ChannelEventAgencyDataBuilder multiVenue(Boolean multiVenue) {
        this.multiVenue = multiVenue;
        return this;
    }

    public ChannelEventAgencyDataBuilder multiLocation(Boolean multiLocation) {
        this.multiLocation = multiLocation;
        return this;
    }

    public ChannelEventAgencyDataBuilder catalogInfo(ChannelCatalogEventInfo catalogInfo) {
        this.catalogInfo = catalogInfo;
        return this;
    }

    public ChannelEventAgencyDataBuilder allowChannelPromotions(Boolean allowChannelPromotions) {
        this.allowChannelPromotions = allowChannelPromotions;
        return this;
    }

    public ChannelEventAgencyDataBuilder sessionsShowDate(final Boolean sessionsShowDate) {
        this.sessionsShowDate = sessionsShowDate;
        return this;
    }

    public ChannelEventAgencyDataBuilder sessionsShowDateTime(final Boolean sessionsShowDateTime) {
        this.sessionsShowDateTime = sessionsShowDateTime;
        return this;
    }

    public ChannelEventAgencyDataBuilder sessionsShowSchedule(final Boolean sessionsShowSchedule) {
        this.sessionsShowSchedule = sessionsShowSchedule;
        return this;
    }

    public ChannelEventAgencyDataBuilder sessionsNoFinalDate(final Boolean sessionsNoFinalDate) {
        this.sessionsNoFinalDate = sessionsNoFinalDate;
        return this;
    }

    public ChannelEventAgencyDataBuilder ticketHandling(final Boolean ticketHandling) {
        this.ticketHandling = ticketHandling;
        return this;
    }

    public ChannelEventAgencyDataBuilder useAlternativePromoterSurcharges(final Boolean useAlternativeSurcharges) {
        this.useAlternativePromoterSurcharges = useAlternativeSurcharges;
        return this;
    }

    public ChannelEventAgencyDataBuilder hasSessions(final Boolean hasSessions) {
        this.hasSessions = hasSessions;
        return this;
    }

    public ChannelEventAgencyDataBuilder hasSessionPacks(final Boolean hasSessionPacks) {
        this.hasSessionPacks = hasSessionPacks;
        return this;
    }

    public ChannelEventAgencyDataBuilder infoBannerSaleRequest(final Map<String, String> infoBannerSaleRequest) {
        this.infoBannerSaleRequest = infoBannerSaleRequest;
        return this;
    }

    public ChannelEventAgencyDataBuilder mustBeIndexed(Boolean mustBeIndexed) {
        this.mustBeIndexed = mustBeIndexed;
        return this;
    }

    public ChannelEventAgencyDataBuilder firstPublishedSession(final Optional<Timestamp> firstPublishedSession) {
        firstPublishedSession.ifPresent(timestamp -> this.firstPublishedDateSession = timestamp.toLocalDateTime().atZone(ZoneOffset.UTC));
        return this;
    }

    public ChannelEventAgencyDataBuilder firstPublishedSessionPack(final Optional<Timestamp> firstPublishedSessionPack) {
        firstPublishedSessionPack.ifPresent(timestamp -> this.firstPublishedDateSessionPack = timestamp.toLocalDateTime().atZone(ZoneOffset.UTC));
        return this;
    }

    public ChannelEventAgencyDataBuilder channelSubtype(final ChannelSubtype channelSubtype){
        this.channelSubtype = channelSubtype;
        return this;
    }

    public ChannelEventAgencyData build() {
        ChannelEventAgencyData response = buildChannelEventAgencyData(channelId, eventId, agencyId);
        response.setMustBeIndexed(mustBeIndexed);

        ChannelEventAgency channelEvent = new ChannelEventAgency();
        channelEvent.setAgencyId(agencyId);
        channelEvent.setAgencyConditions(agencyConditions);
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
        channelEvent.setFirstPublishedSession(firstPublishedDateSession);
        channelEvent.setFirstPublishedSessionPack(firstPublishedDateSessionPack);
        channelEvent.setInfoBannerSaleRequest(infoBannerSaleRequest);
        channelEvent.setChannelSubtype(channelSubtype);
        response.setChannelEventAgency(channelEvent);
        return response;
    }

    public static ChannelEventAgencyData buildChannelEventAgencyData(Long channelId, Long eventId, Long agencyId) {
        ChannelEventAgencyData response = new ChannelEventAgencyData();
        response.setId(EventDataUtils.getChannelEventAgencyKey(channelId, eventId, agencyId));
        response.setJoin(new JoinField(KEY_CHANNEL_EVENT_AGENCY, EventDataUtils.getEventKey(eventId)));
        return response;
    }

}
