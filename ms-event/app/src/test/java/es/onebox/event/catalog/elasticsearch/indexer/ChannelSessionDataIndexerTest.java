package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.dao.CatalogChannelSessionCouchDao;
import es.onebox.event.catalog.dao.ChannelSessionPriceCouchDao;
import es.onebox.event.catalog.dao.SBSessionsCouchDao;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.SessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.datasources.ms.ticket.repository.SessionRepository;
import es.onebox.event.events.dao.ChannelEventCommunicationElementDao;
import es.onebox.event.events.enums.ChannelEventStatus;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.priceengine.simulation.service.PriceEngineSimulationService;
import es.onebox.event.promotions.service.EventPromotionsService;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ChannelSessionDataIndexerTest {

    private static final Integer EVENT_ID = 877;
    private static final Integer SESSION_ID = 123;
    private static final Integer CHANNEL_ID = 456;

    @Mock
    private SessionElasticDao sessionElasticDao;
    @Mock
    private ChannelSessionElasticDao channelSessionElasticDao;
    @Mock
    private ChannelSessionPriceCouchDao channelSessionPriceCouchDao;
    @Mock
    private CatalogChannelSessionCouchDao catalogChannelSessionCouchDao;
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

    private ChannelSessionDataIndexer channelSessionDataIndexer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.channelSessionDataIndexer = new ChannelSessionDataIndexer(
                sessionElasticDao,
                channelSessionElasticDao,
                channelSessionPriceCouchDao,
                catalogChannelSessionCouchDao,
                sessionRepository,
                priceEngineSimulationService,
                eventPromotionsService,
                sbSessionsCouchDao,
                channelEventCommunicationElementDao,
                localCacheRepository,
                staticDataContainer
        );
    }

    @Test
    void testIndexChannelSessions_whenPartialBasic_shouldUpdateBasicInfo() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_BASIC);

        ChannelSessionData oldValue = buildChannelSessionData();
        when(channelSessionElasticDao.getByEventId(anyLong())).thenReturn(List.of(oldValue));
        when(catalogChannelSessionCouchDao.bulkGet(any())).thenReturn(List.of(oldValue.getChannelSession()));

        Assertions.assertTrue(oldValue.getChannelSession().getForSale());
        Assertions.assertNotNull(oldValue.getChannelSession().getContainerOccupations());

        channelSessionDataIndexer.indexChannelSessions(context);

        verify(catalogChannelSessionCouchDao, times(1)).bulkUpsert(anyList());
        verify(channelSessionElasticDao, times(1)).bulkUpsert(anyBoolean(), anyString(),
                argThat((ArgumentMatcher<ChannelSessionData>) argument ->
                {
                    ChannelSession newValue = argument.getChannelSession();
                    return argument.getMustBeIndexed().equals(true) &&
                            newValue != null &&
                            newValue.getContainerOccupations() == null &&
                            newValue.getForSale().equals(false);
                }));
    }

    @Test
    void testIndexChannelSessions_whenPartialBasic_shouldUpdateBasicInfo_deleteUnpublishedSession() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_BASIC);

        Integer session2Id = 2;
        String session2Key = EventDataUtils.getChannelSessionKey(CHANNEL_ID.longValue(), session2Id.longValue());

        SessionForCatalogRecord session2 = buildSession();
        session2.setIdsesion(session2Id);
        session2.setEstado(SessionStatus.FINALIZED.getId());
        context.getSessions().add(session2);
        context.getAllSessions().add(session2);

        ChannelSessionData oldValue = buildChannelSessionData();
        ChannelSessionData oldValue2 = buildChannelSessionData();
        oldValue2.setId(session2Key);
        oldValue2.getChannelSession().setSessionId(session2Id.longValue());

        when(channelSessionElasticDao.getByEventId(anyLong())).thenReturn(List.of(oldValue, oldValue2));
        when(catalogChannelSessionCouchDao.bulkGet(any())).thenReturn(List.of(oldValue.getChannelSession(), oldValue2.getChannelSession()));

        channelSessionDataIndexer.indexChannelSessions(context);

        verify(catalogChannelSessionCouchDao, times(1)).bulkUpsert(anyList());
        verify(channelSessionElasticDao, times(1)).bulkUpsert(anyBoolean(), anyString(),
                argThat((ArgumentMatcher<ChannelSessionData>) argument ->
                {
                    ChannelSession newValue = argument.getChannelSession();
                    return argument.getMustBeIndexed().equals(true) &&
                            newValue != null &&
                            newValue.getContainerOccupations() == null &&
                            newValue.getForSale().equals(false);
                }));
        verify(channelSessionElasticDao, times(1)).bulkDelete(anyString(),
                argThat((ArgumentMatcher<String>) argument ->
                {
                    return argument.equals(session2Key);
                }));
    }

    @Test
    void testIndexChannelSessions_whenPartialComElements_shouldNotIndex() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_COM_ELEMENTS);
        channelSessionDataIndexer.indexChannelSessions(context);
        verifyNoInteractions(catalogChannelSessionCouchDao);
    }

    private ChannelSessionData buildChannelSessionData() {
        ChannelSessionData channelSessionData = new ChannelSessionData();
        ChannelSession channelSession = new ChannelSession();
        channelSession.setChannelId(CHANNEL_ID.longValue());
        channelSession.setEventId(EVENT_ID.longValue());
        channelSession.setSessionId(SESSION_ID.longValue());
        channelSession.setForSale(true);
        channelSession.setContainerOccupations(new ArrayList<>());
        channelSessionData.setChannelSession(channelSession);
        channelSessionData.setId(EventDataUtils.getChannelSessionKey(CHANNEL_ID.longValue(), SESSION_ID.longValue()));
        channelSessionData.setMustBeIndexed(true);
        return channelSessionData;
    }

    private EventIndexationContext buildContext(EventIndexationType type) {
        CpanelEventoRecord eventRecord = buildEventRecord();
        EventIndexationContext ctx = new EventIndexationContext(eventRecord, type);
        ctx.setChannelSessionsToIndex(new ArrayList<>());
        ctx.setVenueTemplatesBySession(new HashMap<>());
        ctx.setVenueTemplateInfos(new HashMap<>());
        ctx.setVenueDescriptor(new HashMap<>());

        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdcanal(CHANNEL_ID);
        channelEvent.setIdevento(EVENT_ID);
        channelEvent.setEstadorelacion(ChannelEventStatus.ACCEPTED.getId());
        channelEvent.setPublicado((byte) 1);
        ctx.setChannelEvents(List.of(channelEvent));

        ctx.addDocumentIndexed(buildChannelSessionData());
        ctx.setSessions(new ArrayList<>(List.of(buildSession())));
        ctx.setAllSessions(ctx.getSessions());

        return ctx;
    }

    private CpanelEventoRecord buildEventRecord() {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setIdevento(EVENT_ID);
        event.setEstado(EventStatus.READY.getId());
        return event;
    }

    private SessionForCatalogRecord buildSession() {
        SessionForCatalogRecord record = new SessionForCatalogRecord();
        record.setIdsesion(SESSION_ID);
        record.setIdevento(EVENT_ID);
        record.setEstado(3);
        record.setPublicado((byte) 1);
        record.setIspreview(false);
        record.setEnventa((byte) 0);
        return record;
    }
} 
