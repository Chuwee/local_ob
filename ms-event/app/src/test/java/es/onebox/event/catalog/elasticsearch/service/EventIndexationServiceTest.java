package es.onebox.event.catalog.elasticsearch.service;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.attendants.AttendantsConfigService;
import es.onebox.event.attendants.dto.EventAttendantsConfigDTO;
import es.onebox.event.catalog.dao.CatalogChannelPackCouchDao;
import es.onebox.event.catalog.dao.SBSessionsCouchDao;
import es.onebox.event.catalog.dao.TemplateElementInfoCouchDao;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import es.onebox.event.catalog.dto.ChangeSeatAllowedSessions;
import es.onebox.event.catalog.dto.ChangeSeatAmountType;
import es.onebox.event.catalog.dto.ChangeSeatChangeType;
import es.onebox.event.catalog.dto.ChangeSeatRefundType;
import es.onebox.event.catalog.dto.ChangeSeatTickets;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.context.OccupationIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.ChannelAttributesCouchDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventAgencyElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.EventElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.catalog.service.InvalidableCacheService;
import es.onebox.event.config.LocalCache;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.event.entity.templateszones.dao.EntityTemplatesZonesCommElementDao;
import es.onebox.event.entity.templateszones.dao.EntityTemplatesZonesDao;
import es.onebox.event.events.dao.AttendantFieldDao;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.CollectiveDao;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.SalesGroupAssignmentDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.domain.eventconfig.EventChangeSeatConfig;
import es.onebox.event.events.domain.eventconfig.EventChangeSeatExpiry;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.domain.eventconfig.ChangeSeatNewTicketSelection;
import es.onebox.event.events.domain.eventconfig.ChangeSeatVoucherExpiry;
import es.onebox.event.events.domain.eventconfig.ChangeSeatExpiryTime;
import es.onebox.event.events.domain.eventconfig.ChangeSeatPrice;
import es.onebox.event.events.domain.eventconfig.ChangeSeatRefund;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.ChannelSurchargesTaxesOrigin;
import es.onebox.event.events.prices.EventPriceRecord;
import es.onebox.event.events.prices.EventPricesDao;
import es.onebox.event.events.prices.enums.PriceTypeFilter;
import es.onebox.event.events.service.EventChannelB2BService;
import es.onebox.event.packs.dao.PackChannelDao;
import es.onebox.event.packs.dao.PackDao;
import es.onebox.event.packs.dao.PackItemSubsetDao;
import es.onebox.event.packs.dao.PackItemsDao;
import es.onebox.event.packs.dao.domain.PackChannelItemsRecord;
import es.onebox.event.packs.enums.PackItemSubsetType;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.simulation.dao.EventChannelDao;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.priceengine.surcharges.CatalogSurchargeService;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRange;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRanges;
import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;
import es.onebox.event.products.dao.ProductChannelDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.promotions.service.EventPromotionsService;
import es.onebox.event.seasontickets.dao.SeasonTicketChangeSeatPricesDao;
import es.onebox.event.seasontickets.dao.SeasonTicketDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketReleaseSeatCouchDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfigCouchDao;
import es.onebox.event.sessions.dao.PresaleDao;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.SessionTaxesDao;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.venues.dao.ProviderVenueDao;
import es.onebox.event.venues.dao.ProviderVenueTemplateDao;
import es.onebox.event.venues.dao.VenueDao;
import es.onebox.event.venues.domain.VenueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemSubsetRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class EventIndexationServiceTest {

    private static final Long EVENT_ID = 877L;
    private static final Long SESSION_ID = 99918L;
    private static final Long SESSION2_ID = 99919L;
    private static final Long CHANNEL_ID = 15L;
    private static final Long CHANNEL_EVENT_ID = 777514L;
    private static final Long VENUE_ID = 89L;
    private static final Long QUOTA = 954L;
    private static final Double PRICE = 15.98;
    private static final Double SURCHARGE = 1.42;
    private static final Integer CHANNEL_SURCHARGE_TAX_ID_1 = 456;
    private static final Integer CHANNEL_SURCHARGE_TAX_ID_2 = 789;

    @Mock
    private ChannelAgencyIndexationService channelAgencyIndexationService;
    @Mock
    private ChannelSessionIndexationService channelSessionIndexationService;
    @Mock
    private EventDao eventDao;
    @Mock
    private SessionDao sessionDao;
    @Mock
    private SessionTaxesDao sessionTaxesDao;
    @Mock
    private ChannelEventDao channelEventDao;
    @Mock
    private EventChannelDao eventChannelDao;
    @Mock
    private VenueDao venueDao;
    @Mock
    private ProviderVenueDao providerVenueDao;
    @Mock
    private ProviderVenueTemplateDao providerVenueTemplateDao;
    @Mock
    private ChannelDao channelDao;
    @Mock
    private SalesGroupAssignmentDao salesGroupAssignmentDao;
    @Mock
    private EventElasticDao eventElasticDao;
    @Mock
    private ChannelEventElasticDao channelEventElasticDao;
    @Mock
    private EventPricesDao eventPricesDao;
    @Mock
    private CatalogSurchargeService catalogSurchargeService;
    @Mock
    private AttendantsConfigService attendantsConfigService;
    @Mock
    private VenueDescriptorService venueDescriptorService;
    @Mock
    private AttendantFieldDao attendantFieldDao;
    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private EventConfigCouchDao eventConfigCouchDao;
    @Mock
    private CacheRepository localCacheRepository;
    @Mock
    private ProductChannelDao productChannelDao;
    @Mock
    private PresaleDao presaleDao;
    @Mock
    private CollectiveDao collectiveDao;
    @Mock
    private ChannelAttributesCouchDao channelAttributesCouchDao;
    @Mock
    private EventChannelB2BService eventChannelB2BService;
    @Mock
    private EventPromotionsService eventPromotionsService;
    @Mock
    private TicketsRepository ticketsRepository;
    @Mock
    private ChannelEventAgencyElasticDao channelEventAgencyElasticDao;
    @Mock
    private SBSessionsCouchDao sbSessionsCouchDao;
    @Mock
    private SeasonSessionDao seasonSessionDao;
    @Mock
    private TemplateElementInfoCouchDao templateElementInfoCouchDao;
    @Mock
    private SeasonTicketRenewalConfigCouchDao seasonTicketRenewalConfigCouchDao;
    @Mock
    private SeasonTicketChangeSeatPricesDao seasonTicketChangeSeatPricesDao;
    @Mock
    private SeasonTicketReleaseSeatCouchDao seasonTicketReleaseSeatCouchDao;
    @Mock
    private SeasonTicketDao seasonTicketDao;
    @Mock
    private PackDao packDao;
    @Mock
    private PackItemsDao packItemsDao;
    @Mock
    private PackItemSubsetDao packItemSubsetDao;
    @Mock
    private PackChannelDao packChannelDao;
    @Mock
    private ProductDao productDao;
    @Mock
    EntityTemplatesZonesDao entityTemplatesZonesDao;
    @Mock
    EntityTemplatesZonesCommElementDao entityTemplatesZonesCommElementDao;
    @Mock
    StaticDataContainer staticDataContainer;
    @Mock
    CatalogChannelPackCouchDao catalogChannelPackCouchDao;
    @Mock
    InvalidableCacheService invalidableCacheService;

    private EventIndexationService eventIndexationService;
    private OccupationIndexationService occupationIndexationService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.eventIndexationService = new EventIndexationService(channelSessionIndexationService, channelAgencyIndexationService, sessionDao,
                providerVenueDao, providerVenueTemplateDao,
                sessionTaxesDao, eventChannelDao, eventPricesDao, channelEventDao, channelDao, venueDao, salesGroupAssignmentDao, seasonTicketDao,
                attendantFieldDao, eventElasticDao,
                eventConfigCouchDao, catalogSurchargeService, attendantsConfigService, venueDescriptorService,
                entitiesRepository, localCacheRepository, productChannelDao, presaleDao, collectiveDao, eventPromotionsService,
                channelAttributesCouchDao, ticketsRepository, seasonSessionDao, templateElementInfoCouchDao, packDao, packItemsDao, packItemSubsetDao, packChannelDao, productDao,
                entityTemplatesZonesDao, entityTemplatesZonesCommElementDao, staticDataContainer, catalogChannelPackCouchDao, invalidableCacheService);
        this.occupationIndexationService = new OccupationIndexationService(channelSessionIndexationService, channelAgencyIndexationService,
                eventElasticDao, eventPromotionsService, localCacheRepository, entitiesRepository, channelDao, channelEventDao,
                ticketsRepository, sessionDao, sessionTaxesDao, salesGroupAssignmentDao, eventDao, channelEventElasticDao, channelEventAgencyElasticDao);
    }

    @Test
    void testPrepareEventContext() {
        CpanelEventoRecord event = prepareEvent();
        mockSessions();
        mockChannelEvents();
        mockEventChannels();
        mockChannels();
        mockVenues();
        mockAttendantsConfig();
        mockQuotas();
        mockPrices();
        mockSurcharges();
        mockEventEntity(event);
        EventIndexationContext ctx = eventIndexationService.prepareEventContext(event, null, EventIndexationType.FULL);
        verifyContext(ctx);
    }

    @Test
    void testPrepareEventSessionContext() {
        CpanelEventoRecord event = prepareEvent();
        mockSessions();
        mockChannelEvents();
        mockEventChannels();
        mockChannels();
        mockVenues();
        mockAttendantsConfig();
        mockQuotas();
        mockPrices();
        mockSurcharges();
        mockEventEntity(event);
        EventIndexationContext ctx = eventIndexationService.prepareEventContext(event, SESSION_ID, EventIndexationType.FULL);
        verifyContext(ctx);
    }

    @Test
    void testPrepareOccupationContext() {
        CpanelEventoRecord event = prepareEvent();
        Mockito.when(localCacheRepository.cached(eq(LocalCache.EVENT_TYPE_KEY), anyInt(), any(), any(), eq(new Object[]{EVENT_ID}))).thenReturn(event);
        mockChannelEvents();
        mockEventData();
        mockChannelEventsData();
        OccupationIndexationContext ctx = occupationIndexationService.prepareOccupationContext(EVENT_ID, SESSION_ID);
        verifyContext(ctx);
    }

    @Test
    void testPrepareEventContext_changeSeatConfig() {
        CpanelEventoRecord event = prepareEvent();
        Mockito.when(eventConfigCouchDao.get(String.valueOf(event.getIdevento()))).thenReturn(mockEventConfig(event.getIdevento()));

        EventIndexationContext ctx = eventIndexationService.prepareEventContext(event, null, EventIndexationType.FULL);

        assertTrue(ctx.getEventConfig().getEventChangeSeatConfig().getAllowChangeSeat());
        assertNotNull(ctx.getEventConfig().getEventChangeSeatConfig().getEventChangeSeatExpiry());
    }

    private CpanelEventoRecord prepareEvent() {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setIdevento(EVENT_ID.intValue());
        return event;
    }

    private void mockSessions() {
        SessionForCatalogRecord session = new SessionForCatalogRecord();
        session.setIdsesion(SESSION_ID.intValue());
        session.setEstado(SessionStatus.READY.getId());
        SessionForCatalogRecord session2 = new SessionForCatalogRecord();
        session2.setIdsesion(SESSION2_ID.intValue());
        session2.setEstado(SessionStatus.READY.getId());
        SessionSearchFilter filter = new SessionSearchFilter();
        filter.setEventId(Collections.singletonList(EVENT_ID));
        filter.setIncludeDeleted(true);
        when(sessionDao.findSessionsForCatalog(eq(filter))).thenReturn(List.of(session, session2));
    }

    private void mockChannelEvents() {
        CpanelCanalEventoRecord channelEvent = prepareChannelEvent();
        when(channelEventDao.getChannelEvents(EVENT_ID)).thenReturn(Collections.singletonList(channelEvent));
    }

    private CpanelCanalEventoRecord prepareChannelEvent() {
        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdcanaleevento(CHANNEL_EVENT_ID.intValue());
        channelEvent.setIdevento(EVENT_ID.intValue());
        channelEvent.setIdcanal(CHANNEL_ID.intValue());
        channelEvent.setTodosgruposventa((byte) 0);
        return channelEvent;
    }

    private void mockEventChannels() {
        EventChannelForCatalogRecord eventChannel = prepareEventChannel();
        when(eventChannelDao.getEventChannels(EVENT_ID)).thenReturn(Collections.singletonList(eventChannel));
    }

    private EventChannelForCatalogRecord prepareEventChannel() {
        EventChannelForCatalogRecord eventChannel = new EventChannelForCatalogRecord();
        eventChannel.setIdevento(EVENT_ID.intValue());
        eventChannel.setIdcanal(CHANNEL_ID.intValue());
        ChannelTaxInfo surchargeTax1 = new ChannelTaxInfo();
        surchargeTax1.setId(CHANNEL_SURCHARGE_TAX_ID_1.longValue());
        ChannelTaxInfo surchargeTax2 = new ChannelTaxInfo();
        surchargeTax2.setId(CHANNEL_SURCHARGE_TAX_ID_2.longValue());
        eventChannel.setSurchargesTaxes(List.of(surchargeTax1, surchargeTax2));
        return eventChannel;
    }

    private void mockChannels() {
        when(channelDao.getByIds(List.of(CHANNEL_ID.intValue()))).thenReturn(List.of(
                new ChannelInfo(CHANNEL_ID, "", 1L, ChannelSubtype.PORTAL_WEB.getIdSubtipo(), ChannelSurchargesTaxesOrigin.CHANNEL.getId())));
    }

    private void mockVenues() {
        VenueRecord venue = new VenueRecord();
        venue.setId(VENUE_ID);
        Map<Long, Long> map = Collections.singletonMap(SESSION_ID, VENUE_ID);
        when(sessionDao.getSessionVenueIds(List.of(SESSION_ID, SESSION2_ID))).thenReturn(map);
        when(venueDao.getVenues(map.values())).thenReturn(Collections.singletonList(venue));
    }

    private void mockAttendantsConfig() {
        EventAttendantsConfigDTO eventAttendantsConfig = new EventAttendantsConfigDTO();
        eventAttendantsConfig.setEventId(EVENT_ID);
        eventAttendantsConfig.setActive(true);
        eventAttendantsConfig.setAutofill(false);
        eventAttendantsConfig.setAllChannelsActive(true);
        when(attendantsConfigService.getEventsAttendantConfig(EVENT_ID)).thenReturn(eventAttendantsConfig);
    }

    private void mockQuotas() {
        when(salesGroupAssignmentDao.getChannelEventQuotaIds(CHANNEL_EVENT_ID.intValue()))
                .thenReturn(Collections.singletonList(QUOTA));
    }

    private void mockPrices() {
        EventPriceRecord price = new EventPriceRecord();
        price.setEventId(EVENT_ID.intValue());
        price.setPrice(PRICE);
        when(eventPricesDao.getBasePricesByEventId(EVENT_ID, PriceTypeFilter.INDIVIDUAL)).thenReturn(Collections.singletonList(price));
    }

    private void mockSurcharges() {
        ChannelEventSurcharges surcharges = new ChannelEventSurcharges();
        SurchargeRanges channelSurcharges = new SurchargeRanges();
        SurchargeRange surchargeRange = new SurchargeRange();
        surchargeRange.setFixedValue(SURCHARGE);
        channelSurcharges.setMain(Collections.singletonList(surchargeRange));
        surcharges.setChannel(channelSurcharges);
        when(catalogSurchargeService.getSurchargeRangesByChannelEventRelationShips(prepareChannelEvent(), prepareEventChannel()))
                .thenReturn(surcharges);
    }

    private void mockEventEntity(CpanelEventoRecord eventoRecord) {
        EntityDTO eventEntity = new EntityDTO();
        eventEntity.setId(eventoRecord.getIdentidad());
        when(entitiesRepository.getEntity(Mockito.anyInt()))
                .thenReturn(eventEntity);
    }

    private void mockEventData() {
        EventData eventData = new EventData();
        Event event = new Event();
        event.setEventId(EVENT_ID);
        eventData.setEvent(event);
        when(eventElasticDao.get(EVENT_ID)).thenReturn(eventData);
    }

    private void mockChannelEventsData() {
        ChannelEventData channelEventData = new ChannelEventData();
        ChannelEvent channelEvent = new ChannelEvent();
        channelEvent.setEventId(EVENT_ID);
        channelEvent.setChannelId(CHANNEL_ID);
        channelEventData.setChannelEvent(channelEvent);
        when(channelEventElasticDao.getByEventId(EVENT_ID)).thenReturn(Collections.singletonList(channelEventData));
    }

    private static EventConfig mockEventConfig(Integer eventId) {
        EventConfig eventConfig = new EventConfig();
        eventConfig.setEventId(eventId);
        eventConfig.setEventChangeSeatConfig(mockEventChangeSeatConfig());
        return eventConfig;
    }

    private static EventChangeSeatConfig mockEventChangeSeatConfig() {
        EventChangeSeatConfig eventChangeSeatConfig = new EventChangeSeatConfig();
        eventChangeSeatConfig.setAllowChangeSeat(true);
        eventChangeSeatConfig.setChangeType(ChangeSeatChangeType.ALL);
        eventChangeSeatConfig.setEventChangeSeatExpiry(new EventChangeSeatExpiry());
        eventChangeSeatConfig.getEventChangeSeatExpiry().setTimeOffsetLimitAmount(1);
        eventChangeSeatConfig.getEventChangeSeatExpiry().setTimeOffsetLimitUnit(ChronoUnit.MONTHS);
        eventChangeSeatConfig.setNewTicketSelection(new ChangeSeatNewTicketSelection());
        eventChangeSeatConfig.getNewTicketSelection().setTickets(ChangeSeatTickets.ANY);
        eventChangeSeatConfig.getNewTicketSelection().setAllowedSessions(ChangeSeatAllowedSessions.ANY);
        eventChangeSeatConfig.getNewTicketSelection().setPrice(new ChangeSeatPrice());
        eventChangeSeatConfig.getNewTicketSelection().getPrice().setType(ChangeSeatAmountType.ANY);
        eventChangeSeatConfig.getNewTicketSelection().getPrice().setRefund(new ChangeSeatRefund());
        eventChangeSeatConfig.getNewTicketSelection().getPrice().getRefund().setType(ChangeSeatRefundType.VOUCHER);
        eventChangeSeatConfig.getNewTicketSelection().getPrice().getRefund().setVoucherExpiry(new ChangeSeatVoucherExpiry());
        eventChangeSeatConfig.getNewTicketSelection().getPrice().getRefund().getVoucherExpiry().setEnabled(true);
        eventChangeSeatConfig.getNewTicketSelection().getPrice().getRefund().getVoucherExpiry().setExpiryTime(new ChangeSeatExpiryTime());
        eventChangeSeatConfig.getNewTicketSelection().getPrice().getRefund().getVoucherExpiry().getExpiryTime().setTimeOffsetLimitAmount(1);
        eventChangeSeatConfig.getNewTicketSelection().getPrice().getRefund().getVoucherExpiry().getExpiryTime().setTimeOffsetLimitUnit(ChronoUnit.MONTHS);
        return eventChangeSeatConfig;
    }

    private void verifyContext(EventIndexationContext ctx) {
        assertNotNull(ctx);
        assertEquals(EVENT_ID, ctx.getEventId());
        verifySessions(ctx.getSessions(), ctx.getSessionFilter());
        verifyChannelEvents(ctx.getChannelEvents());
        verifyEventChannelByChannelId(ctx);
        verifyChannels(ctx.getChannels());
        verifyVenues(ctx.getVenues());
        verifyEventAttendantsConfig(ctx.getEventAttendantsConfig());
        verifyVenueBySessionId(ctx);
        verifyQuotas(ctx.getQuotasByChannel(CHANNEL_ID));
        verifyPrices(ctx.getPrices());
        verifyChannelSurcharges(ctx.getChannelSurcharges());
    }

    private void verifySessions(List<SessionForCatalogRecord> sessions, Long sessionFilter) {
        assertNotNull(sessions);
        if (sessionFilter == null) {
            assertEquals(2, sessions.size());
            CpanelSesionRecord session = sessions.get(0);
            assertEquals(SESSION_ID.longValue(), session.getIdsesion().longValue());
            CpanelSesionRecord session2 = sessions.get(1);
            assertEquals(SESSION2_ID.longValue(), session2.getIdsesion().longValue());
        } else {
            assertEquals(1, sessions.size());
            CpanelSesionRecord session = sessions.get(0);
            assertEquals(SESSION_ID.longValue(), session.getIdsesion().longValue());
        }
    }

    private void verifyChannelEvents(List<CpanelCanalEventoRecord> channelEvents) {
        assertNotNull(channelEvents);
        assertEquals(1, channelEvents.size());
        CpanelCanalEventoRecord channelEvent = channelEvents.get(0);
        assertEquals(EVENT_ID.longValue(), channelEvent.getIdevento().longValue());
        assertEquals(CHANNEL_ID.longValue(), channelEvent.getIdcanal().longValue());
    }

    private void verifyEventChannelByChannelId(EventIndexationContext ctx) {
        Optional<EventChannelForCatalogRecord> eventChannelOpt = ctx.getEventChannel(CHANNEL_ID.intValue());
        assertNotNull(eventChannelOpt);
        assertTrue(eventChannelOpt.isPresent());
        CpanelEventoCanalRecord eventChannel = eventChannelOpt.get();
        assertEquals(EVENT_ID.longValue(), eventChannel.getIdevento().longValue());
        assertEquals(CHANNEL_ID.longValue(), eventChannel.getIdcanal().longValue());
    }

    private void verifyChannels(Map<Long, ChannelInfo> channels) {
        assertNotNull(channels);
        assertEquals(1, channels.size());
        ChannelInfo channel = channels.get(CHANNEL_ID);
        assertEquals(CHANNEL_ID.intValue(), channel.getId().intValue());
        assertEquals(ChannelSubtype.PORTAL_WEB.getIdSubtipo(), channel.getSubtypeId());
    }

    private void verifyVenues(List<VenueRecord> venues) {
        assertNotNull(venues);
        assertEquals(1, venues.size());
        VenueRecord venue = venues.get(0);
        assertEquals(VENUE_ID, venue.getId());
    }

    private void verifyEventAttendantsConfig(EventAttendantsConfigDTO eventAttendantsConfig) {
        assertNotNull(eventAttendantsConfig);
        assertEquals(EVENT_ID, eventAttendantsConfig.getEventId());
        assertEquals(true, eventAttendantsConfig.getAllChannelsActive());
    }

    private void verifyVenueBySessionId(EventIndexationContext ctx) {
        Optional<VenueRecord> venueOpt = ctx.getVenueBySessionId(SESSION_ID);
        assertNotNull(venueOpt);
        assertTrue(venueOpt.isPresent());
        VenueRecord venue = venueOpt.get();
        assertEquals(VENUE_ID, venue.getId());
    }

    private void verifyQuotas(List<Long> quotas) {
        assertNotNull(quotas);
        assertEquals(1, quotas.size());
        assertEquals(QUOTA, quotas.get(0));
    }

    private void verifyPrices(List<EventPriceRecord> prices) {
        assertNotNull(prices);
        assertEquals(1, prices.size());
        EventPriceRecord price = prices.get(0);
        assertEquals(PRICE, price.getPrice());
    }

    private void verifyChannelSurcharges(Map<Long, ChannelEventSurcharges> channelSurcharges) {
        assertNotNull(channelSurcharges);
        assertEquals(1, channelSurcharges.size());
        ChannelEventSurcharges surcharges = channelSurcharges.get(CHANNEL_ID);
        assertNotNull(surcharges);
        SurchargeRanges surchargeChannel = surcharges.getChannel();
        assertNotNull(surchargeChannel);
        List<SurchargeRange> main = surchargeChannel.getMain();
        assertNotNull(main);
        assertEquals(1, main.size());
        SurchargeRange surchargeRange = main.get(0);
        assertNotNull(surchargeRange);
        assertEquals(SURCHARGE, surchargeRange.getFixedValue());
    }

    private void verifyContext(OccupationIndexationContext ctx) {
        assertNotNull(ctx);
        assertEquals(EVENT_ID, ctx.getEventId());
        assertEquals(SESSION_ID, ctx.getSessionId());
        verifyEventData(ctx.getEventData());
        verifyChannelEventsData(ctx.getChannelEvents());
    }

    private void verifyEventData(EventData eventData) {
        assertNotNull(eventData);
        Event event = eventData.getEvent();
        assertNotNull(event);
        assertEquals(EVENT_ID, event.getEventId());
    }

    private void verifyChannelEventsData(List<ChannelEventData> channelEvents) {
        assertNotNull(channelEvents);
        assertEquals(1, channelEvents.size());
        ChannelEventData channelEventData = channelEvents.get(0);
        assertNotNull(channelEventData);
        ChannelEvent channelEvent = channelEventData.getChannelEvent();
        assertNotNull(channelEvent);
        assertEquals(EVENT_ID, channelEvent.getEventId());
        assertEquals(CHANNEL_ID, channelEvent.getChannelId());
    }

    @Test
    void testPrepareRelatedPacks_EventMainItemWithSessionSubsets_ShouldCreateChannelPack() {
        Long packId = 100L;
        Integer packItemId = 200;
        Long sessionId1 = 301L;
        Long sessionId2 = 302L;

        CpanelEventoRecord event = prepareEvent();
        mockSessions();
        mockChannelEvents();
        mockEventChannels();
        mockChannels();
        mockVenues();
        mockAttendantsConfig();
        mockQuotas();
        mockPrices();
        mockSurcharges();
        mockEventEntity(event);

        CpanelPackRecord pack = new CpanelPackRecord();
        pack.setIdpack(packId.intValue());
        IdNameDTO sessionIdName = new IdNameDTO();
        sessionIdName.setId(SESSION_ID);
        Map<IdNameDTO, List<CpanelPackRecord>> packsByIdNameSession = new HashMap<>();
        packsByIdNameSession.put(sessionIdName, List.of(pack));

        when(packDao.getSessionPacks(EVENT_ID.intValue())).thenReturn(packsByIdNameSession);
        when(packDao.getPackIdsWithMainItemTypeEvent(List.of(packId.intValue()))).thenReturn(Set.of(packId.intValue()));

        CpanelPackItemRecord mainPackItemRecord = new CpanelPackItemRecord();
        mainPackItemRecord.setIdpackitem(packItemId);
        mainPackItemRecord.setTipoitem(PackItemType.EVENT.getId());
        mainPackItemRecord.setPrincipal(true);

        when(packItemsDao.getPackMainItemRecordById(packId.intValue())).thenReturn(mainPackItemRecord);

        CpanelPackItemSubsetRecord subsetRecord1 = new CpanelPackItemSubsetRecord();
        subsetRecord1.setIdpackitem(packItemId);
        subsetRecord1.setIdsubitem(sessionId1.intValue());
        subsetRecord1.setType(PackItemSubsetType.SESSION.getId());

        CpanelPackItemSubsetRecord subsetRecord2 = new CpanelPackItemSubsetRecord();
        subsetRecord2.setIdpackitem(packItemId);
        subsetRecord2.setIdsubitem(sessionId2.intValue());
        subsetRecord2.setType(PackItemSubsetType.SESSION.getId());

        when(packItemSubsetDao.getSubsetsByPackItemId(packItemId)).thenReturn(List.of(subsetRecord1, subsetRecord2));
        when(packChannelDao.getAcceptedPackChannelsByPackIdWithItems(anyList())).thenReturn(Collections.emptyList());

        EventIndexationContext ctx = eventIndexationService.prepareEventContext(event, null, EventIndexationType.FULL);

        Map<Integer, ChannelPack> packsWithSessionFilter = ctx.getPacksWithSessionFilterByPackId();
        assertNotNull(packsWithSessionFilter);
        assertEquals(1, packsWithSessionFilter.size());
        ChannelPack channelPack = packsWithSessionFilter.get(packId.intValue());
        assertNotNull(channelPack);
        assertEquals(packId, channelPack.getId());
        assertNotNull(channelPack.getItems());
        assertEquals(2, channelPack.getItems().size());
        assertTrue(channelPack.getItems().stream().anyMatch(item -> item.getItemId().equals(sessionId1)));
        assertTrue(channelPack.getItems().stream().anyMatch(item -> item.getItemId().equals(sessionId2)));
    }
}
