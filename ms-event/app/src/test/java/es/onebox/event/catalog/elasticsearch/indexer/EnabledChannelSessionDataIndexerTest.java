package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.attendants.dto.EventAttendantsConfigDTO;
import es.onebox.event.attendants.dto.SessionAttendantsConfigDTO;
import es.onebox.event.catalog.dao.CatalogChannelSessionCouchDao;
import es.onebox.event.catalog.dao.ChannelSessionPriceCouchDao;
import es.onebox.event.catalog.dao.SBSessionsCouchDao;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.elasticsearch.context.BaseIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionForEventIndexation;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionForOccupationIndexation;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionPriceZones;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.OccupationIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.SessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.JoinField;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionRate;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrix;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationVenueContainer;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearch;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketStatus;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.Ticket;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.ms.ticket.repository.SessionRepository;
import es.onebox.event.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.event.events.dao.ChannelEventCommunicationElementDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.enums.ChannelEventStatus;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.priceengine.simulation.service.PriceEngineSimulationService;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.promotions.dto.EventPromotion;
import es.onebox.event.promotions.dto.restriction.PromotionRestrictions;
import es.onebox.event.promotions.service.EventPromotionsService;
import es.onebox.event.secondarymarket.dto.DatesDTO;
import es.onebox.event.secondarymarket.dto.EnabledChannelDTO;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigDTO;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.venues.domain.VenueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EnabledChannelSessionDataIndexerTest {

    private static final Long EVENT_ID = 12L;
    private static final Long SESSION_ID = 955L;
    private static final Long CHANNEL_ID = 545L;
    private static final Long CHANNEL_EVENT_ID = 98515L;
    private static final Long PROMO_ID = 101L;
    private static final Long PRICE_ZONE = 8526L;
    private static final Long RATE_ID = 9L;
    private static final Long VENUE_ID = 888888L;
    private static final String TIME_ZONE = "America/Curacao";

    @Mock
    private SessionElasticDao sessionElasticDao;
    @Mock
    private ChannelSessionElasticDao channelSessionElasticDao;
    @Mock
    private ChannelSessionPriceCouchDao channelSessionPriceCouchDao;
    @Mock
    private PriceEngineSimulationService priceEngineSimulationService;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private TicketsRepository ticketsRepository;
    @Mock
    private CatalogChannelSessionCouchDao catalogChannelSessionCouchDao;
    @Mock
    private EventPromotionsService eventPromotionsService;
    @Mock
    private SBSessionsCouchDao sbSessionsCouchDao;
    @Mock
    private ChannelEventCommunicationElementDao channelEventCommunicationElementDao;
    @Mock
    private CacheRepository localCacheRepository;
    @Mock
    private StaticDataContainer staticDataContainer;


    private ChannelSessionDataIndexer channelSessionDataIndexer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        channelSessionDataIndexer = new ChannelSessionDataIndexer(sessionElasticDao,
                channelSessionElasticDao,
                channelSessionPriceCouchDao,
                catalogChannelSessionCouchDao,
                sessionRepository,
                priceEngineSimulationService,
                eventPromotionsService,
                sbSessionsCouchDao,
                channelEventCommunicationElementDao,
                localCacheRepository,
                staticDataContainer);
    }

    @Test
    void testIndexChannelSessionsWhenNoChannelSessionsToIndex() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setChannelSessionsToIndex(Collections.emptyList());
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateNoDocumentsIndexed(ctx);
    }

    @Test
    void testIndexChannelSessions() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, true, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWhenSessionNotForSale() {
        EventIndexationContext ctx = prepareEventContext(false, true);
        ctx.setChannelSessionsToIndex(Collections.singletonList(prepareChannelSessionToIndex(false, true, true, true, true, false)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, false, false, true, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWhenChannelEventNotForSale() {
        EventIndexationContext ctx = prepareEventContext(false, true);
        ctx.setChannelSessionsToIndex(Collections.singletonList(prepareChannelSessionToIndex(true, false, true, true, true, false)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, false, false, true, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWhenChannelEventForSaleOnlySecMkt() {
        EventIndexationContext ctx = prepareEventContext(true, true);
        ctx.setChannelSessionsToIndex(Collections.singletonList(prepareChannelSessionToIndex(true, false, true, true, true, true)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, true, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWhenChannelEventForSaleOnlyPrimaryMkt() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setChannelSessionsToIndex(Collections.singletonList(prepareChannelSessionToIndex(true, true, true, true, true, false)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, true, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWhenChannelEventForSale() {
        EventIndexationContext ctx = prepareEventContext(true, false);
        ctx.setChannelSessionsToIndex(Collections.singletonList(prepareChannelSessionToIndex(true, true, true, true, true, true)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, true, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWithPromotionRestrictedBySession() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setEventPromotions(Collections.singletonList(preparePromotion(0L, null, null, null)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, false, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWithPromotionNoRestrictedBySession() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setEventPromotions(Collections.singletonList(preparePromotion(SESSION_ID, null, null, null)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, true, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWithPromotionRestrictedByChannel() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setEventPromotions(Collections.singletonList(preparePromotion(null, 0L, null, null)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, false, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWithPromotionNoRestrictedByChannel() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setEventPromotions(Collections.singletonList(preparePromotion(null, CHANNEL_ID, null, null)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, true, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWithPromotionRestrictedByPriceZone() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setEventPromotions(Collections.singletonList(preparePromotion(null, null, 0L, null)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, false, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWithPromotionNoRestrictedByPriceZone() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setEventPromotions(Collections.singletonList(preparePromotion(null, null, PRICE_ZONE, null)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, true, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWithPromotionRestrictedByRate() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setEventPromotions(Collections.singletonList(preparePromotion(null, null, null, 0L)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, false, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWithPromotionNoRestrictedByRate() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setEventPromotions(Collections.singletonList(preparePromotion(null, null, null, RATE_ID)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, true, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWhenIsSoldOut() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setChannelSessionsToIndex(Collections.singletonList(prepareChannelSessionToIndex(true, true, false, true, true, false)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, true, true, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWithDefaultTimeZone() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setVenuesBySession(Collections.emptyMap());
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, true, ZoneOffset.UTC.getId(), true);
    }

    @Test
    void testIndexChannelSessionsWithNominalEventAndNotNominalSession() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setChannelSessionsToIndex(Collections.singletonList(prepareChannelSessionToIndex(true, true, true, true, false, false)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, true, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWithNotNominalEventAndSession() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setChannelSessionsToIndex(Collections.singletonList(prepareChannelSessionToIndex(true, true, true, false, false, false)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, true, TIME_ZONE, false);
    }

    @Test
    void testIndexChannelSessionsWithNominalEventAndSession() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setChannelSessionsToIndex(Collections.singletonList(prepareChannelSessionToIndex(true, true, true, true, true, false)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, true, TIME_ZONE, true);
    }

    @Test
    void testIndexChannelSessionsWithNotNominalEventAndNominalSession() {
        EventIndexationContext ctx = prepareEventContext(false, false);
        ctx.setChannelSessionsToIndex(Collections.singletonList(prepareChannelSessionToIndex(true, true, true, false, true, false)));
        channelSessionDataIndexer.indexChannelSessions(ctx);
        validateDocumentsIndexed(ctx, true, false, true, TIME_ZONE, true);
    }

    @Test
    void testIndexOccupationWhenNoSoldOut() {
        OccupationIndexationContext ctx = prepareOccupationContext(true, true, false, false);
        channelSessionDataIndexer.indexOccupation(ctx);
        assertEquals(1, ctx.getNumDocumentsIndexed());
        assertFalse(ctx.getDocumentsIndexed(ChannelSessionData.class).get(0).getChannelSession().getSoldOut());
    }

    @Test
    void testIndexOccupationWhenPrimaryMktAndOnlySecMktAvailability() {
        OccupationIndexationContext ctx = prepareOccupationContext(true, false, false, true);
        channelSessionDataIndexer.indexOccupation(ctx);
        assertEquals(1, ctx.getNumDocumentsIndexed());
        assertTrue(ctx.getDocumentsIndexed(ChannelSessionData.class).get(0).getChannelSession().getSoldOut());
    }

    @Test
    void testIndexOccupationWhenSecMktNoSoldOut() {
        OccupationIndexationContext ctx = prepareOccupationContext(false, false, true, true);
        channelSessionDataIndexer.indexOccupation(ctx);
        assertEquals(1, ctx.getNumDocumentsIndexed());
        assertFalse(ctx.getDocumentsIndexed(ChannelSessionData.class).get(0).getChannelSession().getSoldOut());
    }

    @Test
    void testIndexOccupationWhenSoldOut() {
        OccupationIndexationContext ctx = prepareOccupationContext(true, false, false, false);
        channelSessionDataIndexer.indexOccupation(ctx);
        assertEquals(1, ctx.getNumDocumentsIndexed());
        assertTrue(ctx.getDocumentsIndexed(ChannelSessionData.class).get(0).getChannelSession().getSoldOut());
    }

    @Test
    void testIndexOccupationWhenSecMktSoldOut() {
        OccupationIndexationContext ctx = prepareOccupationContext(false, false, true, false);
        channelSessionDataIndexer.indexOccupation(ctx);
        assertEquals(1, ctx.getNumDocumentsIndexed());
        assertTrue(ctx.getDocumentsIndexed(ChannelSessionData.class).get(0).getChannelSession().getSoldOut());
    }

    private EventIndexationContext prepareEventContext(Boolean secMktEnabled, Boolean hasSecMktAvailability) {
        EventIndexationContext ctx = new EventIndexationContext(prepareEvent());
        ctx.setChannelSessionsToIndex(Collections.singletonList(prepareChannelSessionToIndex(true, true, true, true, true, secMktEnabled)));
        ctx.setEventPromotions(Collections.singletonList(preparePromotion(null, null, null, null)));
        ctx.setPrices(Collections.emptyList());
        ctx.setVenueTemplatePrices(Collections.emptyList());
        ctx.setVenuesBySession(Collections.singletonMap(SESSION_ID, VENUE_ID));
        ctx.setVenueTemplatesBySession(Collections.singletonMap(SESSION_ID, VENUE_ID));
        ctx.setChannels(Collections.singletonMap(CHANNEL_ID, prepareChannel()));
        ctx.setEventAttendantsConfig(prepareEventAttendantsConfig());
        ctx.setVenues(Collections.singletonList(prepareVenue()));
        ctx.addDocumentIndexed(prepareSessionData());
        when(channelSessionElasticDao.getByEventId(EVENT_ID)).thenReturn(Collections.singletonList(prepareChannelSession()));
        ctx.setRatesBySession(Collections.singletonMap(SESSION_ID, Set.of(new es.onebox.event.sessions.domain.SessionRate(SESSION_ID, RATE_ID.intValue(), true))));
        ctx.setDefaultRateBySession(Collections.singletonMap(SESSION_ID, RATE_ID));
        ctx.setVenueDescriptor(prepareVenueDescriptor());
        ctx.setEventSecondaryMarketConfig(prepareEventSecMktConfig(CHANNEL_ID, secMktEnabled));
        ctx.setSecondaryMarketForSale(prepareSecMktItems(hasSecMktAvailability));
        ctx.setAllSessionTaxes(new ArrayList<>());
        return ctx;
    }

    private Map<Integer, VenueDescriptor> prepareVenueDescriptor() {
        VenueDescriptor vd = new VenueDescriptor();
        vd.setEventId(EVENT_ID.intValue());
        vd.setType(1);
        return Map.of(VENUE_ID.intValue(), vd);
    }

    private OccupationIndexationContext prepareOccupationContext(boolean isChannelEventPrimaryMktEnabled, boolean hasAvailability, boolean isChannelEventSecondaryMktEnabled, boolean hasSecMktAvailability) {
        OccupationIndexationContext ctx = new OccupationIndexationContext(prepareEvent(), null);
        ctx.setChannelSessionsToIndex(Collections.singletonList(prepareChannelSessionToIndex(hasAvailability)));
        ctx.setEventPromotions(new ArrayList<>());

        EventData eventData = new EventData();
        eventData.setEvent(new Event());
        eventData.getEvent().setEventId(EVENT_ID);
        eventData.getEvent().setPrices(new ArrayList<>());

        ChannelEventData channelEventData = new ChannelEventData();
        channelEventData.setChannelEvent(new ChannelEvent());
        channelEventData.getChannelEvent().setPurchaseChannelEvent(isChannelEventPrimaryMktEnabled);
        channelEventData.getChannelEvent().setPurchaseSecondaryMarketChannelEvent(isChannelEventSecondaryMktEnabled);
        channelEventData.getChannelEvent().setChannelId(CHANNEL_ID);
        channelEventData.getChannelEvent().setSurcharges(new ChannelEventSurcharges());

        SessionData sessionData = new SessionData();
        sessionData.setSession(new Session());
        sessionData.getSession().setSessionId(SESSION_ID);
        sessionData.getSession().setRates(new ArrayList<>());

        ctx.setEventData(eventData);
        ctx.setChannelEvents(Collections.singletonList(channelEventData));

        ctx.setSecondaryMarketForSale(prepareSecMktItems(hasSecMktAvailability));

        when(sessionElasticDao.get(SESSION_ID, EVENT_ID)).thenReturn(sessionData);
        return ctx;
    }

    private ChannelSessionData prepareChannelSession() {
        ChannelSessionData channelSessionData = new ChannelSessionData();
        channelSessionData.setId(EventDataUtils.getChannelSessionKey(CHANNEL_ID, SESSION_ID));
        ChannelSession channelSession = new ChannelSession();
        channelSession.setEventId(EVENT_ID);
        channelSession.setChannelId(CHANNEL_ID);
        channelSession.setSessionId(SESSION_ID);
        channelSession.setSoldOut(false);
        channelSession.setPrices(new PriceMatrix());
        channelSession.setSecondaryMarketConfig(prepareEventSecMktConfig(CHANNEL_ID, true));
        channelSession.setContainerOccupations(fillContainerOccupations());
        channelSessionData.setChannelSession(channelSession);
        return channelSessionData;
    }

    private CpanelEventoRecord prepareEvent() {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setIdevento(EVENT_ID.intValue());
        event.setEstado(EventStatus.READY.getId());
        event.setTipoevento(EventType.NORMAL.getId());
        return event;
    }

    private ChannelInfo prepareChannel() {
        return new ChannelInfo(CHANNEL_ID, "", 1L, ChannelSubtype.PORTAL_WEB.getIdSubtipo(), null);
    }

    private EventAttendantsConfigDTO prepareEventAttendantsConfig() {
        EventAttendantsConfigDTO eventAttendantsConfig = new EventAttendantsConfigDTO();
        eventAttendantsConfig.setEventId(EVENT_ID);
        eventAttendantsConfig.setActive(true);
        eventAttendantsConfig.setAutofill(false);
        eventAttendantsConfig.setAllChannelsActive(true);
        return eventAttendantsConfig;
    }

    private SessionAttendantsConfigDTO prepareSessionAttendantsConfig() {
        SessionAttendantsConfigDTO sessionAttendantsConfig = new SessionAttendantsConfigDTO();
        sessionAttendantsConfig.setSessionId(SESSION_ID);
        sessionAttendantsConfig.setActive(true);
        sessionAttendantsConfig.setAutofill(false);
        sessionAttendantsConfig.setAllChannelsActive(true);
        return sessionAttendantsConfig;
    }

    private VenueRecord prepareVenue() {
        VenueRecord venue = new VenueRecord();
        venue.setId(VENUE_ID);
        venue.setTimeZone(TIME_ZONE);
        return venue;
    }

    private ChannelSessionForEventIndexation prepareChannelSessionToIndex(boolean sessionForSale, boolean channelEventForSale, boolean hasAvailability,
                                                                          boolean eventAttendants, boolean sessionAttendants, boolean secMktEnabled) {
        ChannelSessionForEventIndexation channelSession = new ChannelSessionForEventIndexation();
        fillChannelSessionToIndex(channelSession, hasAvailability);
        channelSession.setSession(prepareSession(sessionForSale));
        channelSession.setChannelEvent(prepareChannelEvent(channelEventForSale));
        channelSession.setEventAttendantsConfig(eventAttendants ? prepareEventAttendantsConfig() : null);
        channelSession.setSessionAttendantsConfig(sessionAttendants ? prepareSessionAttendantsConfig() : null);
        channelSession.setChannel(prepareChannel());
        channelSession.setMustBeIndexed(Boolean.TRUE);
        channelSession.setSecondaryMarketConfig(prepareSessionSecMktConfig(secMktEnabled));
        return channelSession;
    }

    private EventSecondaryMarketConfigDTO prepareEventSecMktConfig(Long channelID, Boolean secMktEnabled) {
        EventSecondaryMarketConfigDTO eventSecondaryMarketConfig = new EventSecondaryMarketConfigDTO();
        DatesDTO secMktDates = new DatesDTO();
        secMktDates.setEnabled(secMktEnabled);
        secMktDates.setStartDate(ZonedDateTime.now());
        secMktDates.setEndDate(ZonedDateTime.now());
        EnabledChannelDTO enabledChannelDTO = new EnabledChannelDTO(channelID);
        enabledChannelDTO.setId(channelID);
        enabledChannelDTO.setStartDate(ZonedDateTime.now());
        enabledChannelDTO.setEndDate(ZonedDateTime.now());
        eventSecondaryMarketConfig.setEnabled(secMktEnabled);
        eventSecondaryMarketConfig.setEnabledChannels(List.of(enabledChannelDTO));
        eventSecondaryMarketConfig.setDates(secMktDates);
        return secMktEnabled ? eventSecondaryMarketConfig : null;
    }

    private SessionSecondaryMarketConfigDTO prepareSessionSecMktConfig(Boolean secMktEnabled) {
        SessionSecondaryMarketConfigDTO secondaryMarketConfig = new SessionSecondaryMarketConfigDTO();
        DatesDTO secMktDates = new DatesDTO();
        secMktDates.setEnabled(secMktEnabled);
        secMktDates.setStartDate(ZonedDateTime.now());
        secMktDates.setEndDate(ZonedDateTime.now());
        secondaryMarketConfig.setDates(secMktDates);
        return secMktEnabled ? secondaryMarketConfig : null;
    }

    private ChannelSessionForOccupationIndexation prepareChannelSessionToIndex(boolean hasAvailability) {
        ChannelSessionForOccupationIndexation channelSessionToIndex = new ChannelSessionForOccupationIndexation();
        fillChannelSessionToIndex(channelSessionToIndex, hasAvailability);
        channelSessionToIndex.setContainerOccupations(fillContainerOccupations());
        channelSessionToIndex.setChannelSessionIndexed(prepareChannelSession().getChannelSession());
        return channelSessionToIndex;
    }

    private void fillChannelSessionToIndex(ChannelSessionPriceZones channelSession, boolean hasAvailability) {
        channelSession.setSessionId(SESSION_ID);
        channelSession.setChannelId(CHANNEL_ID);
        channelSession.setPriceZones(Collections.singletonList(PRICE_ZONE));
        channelSession.setPriceZonesWithAvailability(hasAvailability ? Collections.singletonList(PRICE_ZONE) : Collections.emptyList());
    }

    private CpanelSesionRecord prepareSession(boolean forSale) {
        CpanelSesionRecord session = new CpanelSesionRecord();
        session.setIdsesion(SESSION_ID.intValue());
        session.setEstado(SessionStatus.READY.getId());
        session.setPublicado((byte) 1);
        session.setEnventa((byte) (forSale ? 1 : 0));
        session.setIspreview(false);
        return session;
    }

    private CpanelCanalEventoRecord prepareChannelEvent(boolean forSale) {
        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdcanaleevento(CHANNEL_EVENT_ID.intValue());
        channelEvent.setIdevento(EVENT_ID.intValue());
        channelEvent.setIdcanal(CHANNEL_ID.intValue());
        channelEvent.setEstadorelacion(ChannelEventStatus.ACCEPTED.getId());
        channelEvent.setPublicado((byte) 1);
        channelEvent.setEnventa((byte) (forSale ? 1 : 0));
        channelEvent.setTodosgruposventa((byte) 1);
        return channelEvent;
    }

    private EventPromotion preparePromotion(Long session, Long channel, Long priceZone, Long rate) {
        PromotionRestrictions restrictions = new PromotionRestrictions();
        restrictions.setSessions(session == null ? null : Collections.singletonList(session));
        restrictions.setChannels(channel == null ? null : Collections.singletonList(channel));
        restrictions.setPriceZones(priceZone == null ? null : Collections.singletonList(priceZone));
        restrictions.setRates(rate == null ? null : Collections.singletonList(rate));
        EventPromotion promotion = new EventPromotion();
        promotion.setEventPromotionTemplateId(PROMO_ID);
        promotion.setRestrictions(restrictions);
        return promotion;
    }

    private SessionData prepareSessionData() {
        SessionData sessionData = new SessionData();
        Session session = new Session();
        session.setSessionId(SESSION_ID);
        SessionRate rate = new SessionRate();
        rate.setId(RATE_ID);
        rate.setDefaultRate(true);
        session.setRates(Collections.singletonList(rate));
        sessionData.setSession(session);
        return sessionData;
    }

    private List<SecondaryMarketSearch> prepareSecMktItems(boolean hasSecMktAvailability) {
        SecondaryMarketSearch secondaryMarketSearch = new SecondaryMarketSearch();
        Ticket ticket = new Ticket();
        ticket.setSessionId(SESSION_ID);
        secondaryMarketSearch.setTicket(ticket);
        secondaryMarketSearch.setStatus(SecondaryMarketStatus.FOR_SALE);
        return hasSecMktAvailability ? List.of(secondaryMarketSearch) : null;
    }

    private List<SessionOccupationVenueContainer> fillContainerOccupations() {
        SessionOccupationVenueContainer sessionOccupationVenueContainer = new SessionOccupationVenueContainer();
        SessionOccupationDTO sessionOccupation = new SessionOccupationDTO();
        Map<TicketStatus, Long> statusMap = new HashMap<>();
        statusMap.merge(TicketStatus.SOLD, 0L, Long::sum);
        sessionOccupation.setStatus(statusMap);
        sessionOccupationVenueContainer.setOccupation(sessionOccupation);
        return List.of(sessionOccupationVenueContainer);
    }

    private void validateNoDocumentsIndexed(BaseIndexationContext<?, ?> ctx) {
        List<ChannelSessionData> documents = ctx.getDocumentsIndexed(ChannelSessionData.class);
        assertNotNull(documents);
        assertTrue(documents.isEmpty());
        assertEquals(1, ctx.getNumDocumentsIndexed());
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(channelSessionElasticDao, times(1)).bulkDelete(eq("event|" + EVENT_ID), argumentCaptor.capture());
        assertEquals(EventDataUtils.getChannelSessionKey(CHANNEL_ID, SESSION_ID), argumentCaptor.getValue());
    }

    private void validateDocumentsIndexed(EventIndexationContext ctx, boolean forSale, boolean soldOut, boolean withPromotion,
                                          String timeZone, boolean mandatoryAttendants) {
        List<ChannelSessionData> documents = ctx.getDocumentsIndexed(ChannelSessionData.class);
        assertNotNull(documents);
        assertEquals(1, documents.size());
        assertEquals(2, ctx.getNumDocumentsIndexed());
        validateDocumentIndexed(documents.get(0), forSale, soldOut, withPromotion, timeZone, mandatoryAttendants);
        verify(channelSessionElasticDao, times(0)).bulkDelete(eq("event|" + EVENT_ID), any());
    }

    private void validateDocumentIndexed(ChannelSessionData channelSessionData, boolean forSale, boolean soldOut, boolean withPromotion,
                                         String timeZone, boolean mandatoryAttendants) {
        assertNotNull(channelSessionData);
        assertEquals("channelSession|" + CHANNEL_ID + "|" + SESSION_ID, channelSessionData.getId());
        JoinField join = channelSessionData.getJoin();
        assertNotNull(join);
        assertEquals("channelSession", join.getName());
        assertEquals("session|" + SESSION_ID, join.getParent());
        validateChannelSession(channelSessionData.getChannelSession(), forSale, soldOut, withPromotion, timeZone, mandatoryAttendants);
    }

    private void validateChannelSession(ChannelSession channelSession, boolean forSale, boolean soldOut, boolean withPromotion,
                                        String timeZone, boolean mandatoryAttendants) {
        assertNotNull(channelSession);
        assertEquals(SESSION_ID, channelSession.getSessionId());
        assertEquals(CHANNEL_ID, channelSession.getChannelId());
        assertEquals(EVENT_ID, channelSession.getEventId());
        assertEquals(forSale, channelSession.getForSale());
        assertEquals(soldOut, channelSession.getSoldOut());
        assertEquals(withPromotion ? Collections.singletonList(PROMO_ID) : Collections.emptyList(), channelSession.getPromotions());
        assertEquals(timeZone, channelSession.getTimeZone());
        assertNotNull(channelSession.getDate());
        assertEquals(mandatoryAttendants, channelSession.getMandatoryAttendants());
    }
}
