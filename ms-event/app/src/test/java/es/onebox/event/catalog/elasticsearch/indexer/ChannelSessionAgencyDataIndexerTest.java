package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.dao.CatalogChannelSessionAgencyCouchDao;
import es.onebox.event.catalog.dao.SBSessionsCouchDao;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionAgencyForEventIndexation;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionAgencyElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.SessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.ChannelAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyData;
import es.onebox.event.datasources.ms.ticket.repository.SessionRepository;
import es.onebox.event.events.dao.ChannelEventCommunicationElementDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.enums.ChannelEventStatus;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.priceengine.simulation.service.PriceEngineSimulationService;
import es.onebox.event.promotions.service.EventPromotionsService;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ChannelSessionAgencyDataIndexerTest {

    private static final Integer EVENT_ID = 877;
    private static final Integer SESSION_ID = 123;
    private static final Integer CHANNEL_ID = 456;
    private static final Integer AGENCY_ID = 789;

    @Mock
    private SessionElasticDao sessionElasticDao;
    @Mock
    private ChannelSessionAgencyElasticDao channelSessionAgencyElasticDao;
    @Mock
    private CatalogChannelSessionAgencyCouchDao catalogChannelSessionAgencyCouchDao;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private PriceEngineSimulationService priceEngineSimulationService;
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

    private ChannelSessionAgencyDataIndexer channelSessionAgencyDataIndexer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.channelSessionAgencyDataIndexer = new ChannelSessionAgencyDataIndexer(
                eventPromotionsService,
                sessionElasticDao,
                sessionRepository,
                priceEngineSimulationService,
                sbSessionsCouchDao,
                catalogChannelSessionAgencyCouchDao,
                channelSessionAgencyElasticDao,
                channelEventCommunicationElementDao,
                localCacheRepository,
                staticDataContainer
        );
    }

    @Test
    void testIndexChannelAgencySessions_whenPartialBasic_shouldUpdateBasicInfo() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_BASIC);

        ChannelSessionAgencyData oldValue = buildChannelSessionAgencyData();
        when(channelSessionAgencyElasticDao.getByEventId(anyLong(), anyList())).thenReturn(List.of(oldValue));
        when(catalogChannelSessionAgencyCouchDao.bulkGet(any())).thenReturn(List.of(oldValue.getChannelSessionAgency()));

        Assertions.assertTrue(oldValue.getChannelSessionAgency().getForSale());
        Assertions.assertNotNull(oldValue.getChannelSessionAgency().getContainerOccupations());

        channelSessionAgencyDataIndexer.indexChannelAgencySessions(context);

        verify(catalogChannelSessionAgencyCouchDao, times(1)).upsert(anyString(), any());
        verify(channelSessionAgencyElasticDao, times(1)).upsert(any(), anyString());
    }

    @Test
    void testIndexChannelSessions_whenPartialComElements_shouldNotIndex() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_COM_ELEMENTS);
        channelSessionAgencyDataIndexer.indexChannelAgencySessions(context);
        verifyNoInteractions(catalogChannelSessionAgencyCouchDao);
    }

    @Test
    void testIndexChannelAgencySessions_whenFullUpdate_shouldUpdateFullInfo() {
        EventIndexationContext context = buildContext(EventIndexationType.FULL);

        ChannelSessionAgencyData oldValue = buildChannelSessionAgencyData();
        when(channelSessionAgencyElasticDao.getByEventId(anyLong(), anyList())).thenReturn(List.of(oldValue));
        when(catalogChannelSessionAgencyCouchDao.bulkGet(any())).thenReturn(List.of(oldValue.getChannelSessionAgency()));

        Assertions.assertTrue(oldValue.getChannelSessionAgency().getForSale());
        Mockito.doNothing().when(this.catalogChannelSessionAgencyCouchDao).upsert(Mockito.anyString(), Mockito.any());

        Assertions.assertNotNull(oldValue.getChannelSessionAgency().getContainerOccupations());

        channelSessionAgencyDataIndexer.indexChannelAgencySessions(context);

        verify(catalogChannelSessionAgencyCouchDao, times(1)).upsert(anyString(), any());
        verify(channelSessionAgencyElasticDao, times(1)).upsert(any(), anyString());
    }

    private ChannelSessionAgencyData buildChannelSessionAgencyData() {
        ChannelSessionAgencyData channelSessionData = new ChannelSessionAgencyData();
        ChannelSessionAgency channelSession = new ChannelSessionAgency();
        channelSession.setChannelId(CHANNEL_ID.longValue());
        channelSession.setEventId(EVENT_ID.longValue());
        channelSession.setSessionId(SESSION_ID.longValue());
        channelSession.setAgencyId(AGENCY_ID.longValue());
        channelSession.setForSale(true);
        channelSession.setContainerOccupations(new ArrayList<>());
        channelSessionData.setChannelSessionAgency(channelSession);
        channelSessionData.setMustBeIndexed(true);
        return channelSessionData;
    }

    private EventIndexationContext buildContext(EventIndexationType type) {
        CpanelEventoRecord eventRecord = buildEventRecord();
        EventIndexationContext ctx = new EventIndexationContext(eventRecord, type);
        ctx.setChannelSessionsToIndex(new ArrayList<>());
        ctx.setVenueTemplatesBySession(new HashMap<>());
        ctx.setVenueTemplateInfos(new HashMap<>());
        ctx.setVenueTemplatePrices(new ArrayList<>());
        ctx.setVenueDescriptor(new HashMap<>());

        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdcanal(CHANNEL_ID);
        channelEvent.setIdevento(EVENT_ID);
        channelEvent.setEstadorelacion(ChannelEventStatus.ACCEPTED.getId());
        channelEvent.setPublicado((byte) 1);
        ctx.setChannelEvents(List.of(channelEvent));

        ChannelInfo channelInfo = new ChannelInfo(CHANNEL_ID.longValue(), "", 1L, ChannelSubtype.PORTAL_B2B.getIdSubtipo(), null);
        ctx.setChannels(Map.of(CHANNEL_ID.longValue(), channelInfo));

        ChannelAgency channelAgency = new ChannelAgency();
        channelAgency.setId(AGENCY_ID.longValue());
        ctx.setChannelsWithAgencies(Map.of(CHANNEL_ID.longValue(), Map.of(AGENCY_ID.longValue(), channelAgency)));

        ChannelSessionAgencyForEventIndexation csaForIndexation = new ChannelSessionAgencyForEventIndexation();
        csaForIndexation.setAgencyId(AGENCY_ID.longValue());
        csaForIndexation.setSession(new CpanelSesionRecord());
        csaForIndexation.getSession().setIdsesion(SESSION_ID);
        csaForIndexation.getSession().setEsabono((byte) 0);
        csaForIndexation.getSession().setEnventa((byte) 1);
        csaForIndexation.setChannelEvent(new CpanelCanalEventoRecord());
        csaForIndexation.getChannelEvent().setIdevento(EVENT_ID);
        csaForIndexation.getChannelEvent().setEnventa((byte) 1);
        csaForIndexation.setPriceZonesWithAvailability(new ArrayList<>());
        csaForIndexation.getPriceZonesWithAvailability().add(12L);
        csaForIndexation.setChannelId(CHANNEL_ID.longValue());
        csaForIndexation.setSessionId(SESSION_ID.longValue());
        csaForIndexation.setChannel(new ChannelInfo(CHANNEL_ID.longValue(), "channelName", 12L, 1, null));
        csaForIndexation.setMustBeIndexed(true);
        ctx.setChannelAgencySessionsToIndex(List.of(csaForIndexation));
        ctx.addDocumentIndexed(buildChannelSessionAgencyData());
        ctx.setSessions(List.of(buildSession()));
        ctx.setAllSessions(ctx.getSessions());

        ctx.setVenuesBySession(new HashMap<>());
        ctx.getVenuesBySession().put(1L, 2L);

        return ctx;
    }

    private CpanelEventoRecord buildEventRecord() {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setIdevento(EVENT_ID);
        event.setEstado(EventStatus.READY.getId());
        return event;
    }

    private SessionForCatalogRecord buildSession() {
        SessionForCatalogRecord sessionForCatalogRecord = new SessionForCatalogRecord();
        sessionForCatalogRecord.setIdsesion(SESSION_ID);
        sessionForCatalogRecord.setIdevento(EVENT_ID);
        sessionForCatalogRecord.setEstado(3);
        sessionForCatalogRecord.setPublicado((byte) 1);
        sessionForCatalogRecord.setIspreview(false);
        sessionForCatalogRecord.setEnventa((byte) 0);
        sessionForCatalogRecord.setEsabono((byte) 0);
        return sessionForCatalogRecord;
    }
} 
