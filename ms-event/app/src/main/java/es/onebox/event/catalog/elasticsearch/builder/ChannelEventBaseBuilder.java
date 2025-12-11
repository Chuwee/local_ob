package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventPostBookingQuestions;
import es.onebox.event.events.domain.eventconfig.EventChangeSeatConfig;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class ChannelEventBaseBuilder {

    protected Long channelId;
    protected Long eventId;
    protected Long channelEventId;
    protected Long channelEntityId;
    protected String channelName;
    protected Integer channelEventStatus;
    protected Date publishChannelEventDate;
    protected Date purchaseChannelEventDate;
    protected Boolean publishChannelEvent;
    protected Boolean purchaseChannelEvent;
    protected Boolean purchaseSecondaryMarketChannelEvent;
    protected Date endChannelEventDate;
    protected Boolean eventDates;
    protected Date beginBookingChannelEventDate;
    protected Date endBookingChannelEventDate;
    protected Boolean enabledBookingChannelEvent;
    protected Integer customCategoryId;
    protected String customCategoryName;
    protected String customCategoryCode;
    protected Integer customParentCategoryId;
    protected String customParentCategoryName;
    protected String customParentCategoryCode;
    protected ChannelEventSurcharges surcharges;
    protected List<Long> venueIds;
    protected Boolean multiVenue;
    protected Boolean multiLocation;
    protected ChannelCatalogEventInfo catalogInfo;
    protected Boolean allowChannelPromotions;
    protected Boolean sessionsShowDate;
    protected Boolean sessionsShowDateTime;
    protected Boolean sessionsShowSchedule;
    protected Boolean sessionsNoFinalDate;
    protected Boolean ticketHandling;
    protected Boolean useAlternativePromoterSurcharges;
    protected Boolean hasSessions;
    protected Boolean hasSessionPacks;
    protected ChannelEventPostBookingQuestions postBookingQuestions;
    protected EventChangeSeatConfig eventChangeSeatConfig;
    protected ZonedDateTime firstPublishedDateSession;
    protected ZonedDateTime firstPublishedDateSessionPack;
    protected Map<String, String> infoBannerSaleRequest;
    protected Boolean mustBeIndexed;
    protected Boolean phoneValidationRequired;
    protected Boolean attendantVerificationRequired;
    protected ChannelSubtype channelSubtype;
}
