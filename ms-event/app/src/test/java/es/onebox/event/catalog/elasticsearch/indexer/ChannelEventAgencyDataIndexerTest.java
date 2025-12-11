package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.dao.CatalogChannelEventAgencyCouchDao;
import es.onebox.event.catalog.dao.CatalogChannelSessionAgencyCouchDao;
import es.onebox.event.events.postbookingquestions.dao.PostBookingQuestionCouchDao;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventAgencyElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionAgencyElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.ChannelAgency;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDatesWithTimeZones;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyData;
import es.onebox.event.communicationelements.dao.EmailCommunicationElementDao;
import es.onebox.event.datasources.ms.channel.repository.ChannelsRepository;
import es.onebox.event.events.dao.EventChannelCommElemDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.taxonomy.dao.CustomTaxonomyDao;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChannelEventAgencyDataIndexerTest {

    private static final Integer EVENT_ID = 877;
    private static final Integer CHANNEL_ID = 456;
    private static final Integer SESSION_ID = 123;
    private static final Integer AGENCY_ID = 789;

    @Mock
    private ChannelEventAgencyElasticDao channelEventAgencyElasticDao;
    @Mock
    private ChannelSessionAgencyElasticDao channelSessionAgencyElasticDao;
    @Mock
    private CatalogChannelEventAgencyCouchDao catalogChannelEventAgencyCouchDao;
    @Mock
    private CatalogChannelSessionAgencyCouchDao catalogChannelSessionAgencyCouchDao;
    @Mock
    private CustomTaxonomyDao customTaxonomyDao;
    @Mock
    private EmailCommunicationElementDao emailCommunicationElementDao;
    @Mock
    private CacheRepository localCacheRepository;
    @Mock
    private EventChannelCommElemDao eventChannelCommElemDao;
    @Mock
    private ChannelsRepository channelsRepository;
    @Mock
    private PostBookingQuestionCouchDao postBookingQuestionCouchDao;

    private ChannelEventAgencyDataIndexer channelEventAgencyDataIndexer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.channelEventAgencyDataIndexer = new ChannelEventAgencyDataIndexer(
                customTaxonomyDao,
                emailCommunicationElementDao,
                eventChannelCommElemDao,
                catalogChannelEventAgencyCouchDao,
                channelEventAgencyElasticDao,
                channelSessionAgencyElasticDao,
                catalogChannelSessionAgencyCouchDao,
                channelsRepository,
                localCacheRepository,
                postBookingQuestionCouchDao
        );
    }

    @Test
    void testIndexChannelEvents_whenPartialBasic_shouldUpdateBasicInfo() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_BASIC);

        when(channelEventAgencyElasticDao.getByEventId(anyLong())).thenReturn(new ArrayList<>());

        ChannelEventAgencyData oldValue = buildChannelEventData();
        when(catalogChannelEventAgencyCouchDao.get(anyString(), anyString(), anyString())).thenReturn(oldValue.getChannelEventAgency());
        when(catalogChannelSessionAgencyCouchDao.bulkGet(any())).thenReturn(List.of(buildChannelSessionData().getChannelSessionAgency()));

        Assertions.assertEquals(oldValue.getChannelEventAgency().getHasSessions(), false);
        Assertions.assertEquals(oldValue.getChannelEventAgency().getHasSessionPacks(), false);

        channelEventAgencyDataIndexer.indexChannelAgencyEvents(context);

        verify(catalogChannelEventAgencyCouchDao, times(1)).bulkUpsert(anyList());
        verify(channelEventAgencyElasticDao, times(1)).bulkUpsert(anyBoolean(), anyString(),
                argThat((ArgumentMatcher<ChannelEventAgencyData>) argument ->
                {
                    ChannelEventAgency newValue = argument.getChannelEventAgency();
                    return argument.getMustBeIndexed().equals(true) &&
                            newValue.getHasSessions().equals(true) &&
                            newValue.getHasSessionPacks().equals(false) &&
                            newValue.getCatalogInfo() != null;
                }));
    }

    private EventIndexationContext buildContext(EventIndexationType type) {
        CpanelEventoRecord eventRecord = buildEventRecord();
        EventIndexationContext ctx = new EventIndexationContext(eventRecord, type);

        ctx.setVenueTemplatesBySession(new HashMap<>());
        ctx.setVenueTemplateInfos(new HashMap<>());
        ctx.setVenueDescriptor(new HashMap<>());

        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setIdcanal(CHANNEL_ID);
        channelEvent.setIdevento(EVENT_ID);
        ctx.setChannelEvents(List.of(channelEvent));

        EventChannelForCatalogRecord eventChannel = new EventChannelForCatalogRecord();
        eventChannel.setIdcanal(CHANNEL_ID);
        channelEvent.setIdevento(EVENT_ID);
        ctx.setEventChannels(List.of(eventChannel));

        ChannelInfo channelInfo = new ChannelInfo(CHANNEL_ID.longValue(), "", 1L, ChannelSubtype.PORTAL_B2B.getIdSubtipo(), null);
        ctx.setChannels(Map.of(CHANNEL_ID.longValue(), channelInfo));

        ChannelAgency channelAgency = new ChannelAgency();
        channelAgency.setId(AGENCY_ID.longValue());
        ctx.setChannelsWithAgencies(Map.of(CHANNEL_ID.longValue(), Map.of(AGENCY_ID.longValue(), channelAgency)));

        ctx.addDocumentIndexed(buildChannelSessionData());
        ctx.setSessions(List.of(buildSession()));
        ctx.setAllSessions(ctx.getSessions());

        return ctx;
    }

    private ChannelEventAgencyData buildChannelEventData() {
        ChannelEventAgencyData channelEvent = new ChannelEventAgencyData();
        channelEvent.setMustBeIndexed(true);
        channelEvent.setChannelEventAgency(buildChannelEvent());
        return channelEvent;
    }

    private static ChannelEventAgency buildChannelEvent() {
        ChannelEventAgency channelEvent = new ChannelEventAgency();
        channelEvent.setChannelId(CHANNEL_ID.longValue());
        channelEvent.setEventId(EVENT_ID.longValue());
        channelEvent.setAgencyId(AGENCY_ID.longValue());
        ChannelCatalogEventInfo catalogInfo = new ChannelCatalogEventInfo();
        catalogInfo.setDate(new ChannelCatalogDatesWithTimeZones());
        catalogInfo.getDate().setStart(new Date());
        channelEvent.setHasSessions(false);
        channelEvent.setHasSessionPacks(false);
        channelEvent.setCatalogInfo(catalogInfo);
        return channelEvent;
    }

    private ChannelSessionAgencyData buildChannelSessionData() {
        ChannelSessionAgencyData sessionData = new ChannelSessionAgencyData();
        ChannelSessionAgency channelSession = new ChannelSessionAgency();
        channelSession.setSessionId(SESSION_ID.longValue());
        channelSession.setChannelId(CHANNEL_ID.longValue());
        channelSession.setAgencyId(AGENCY_ID.longValue());
        channelSession.setForSale(true);
        sessionData.setChannelSessionAgency(channelSession);
        return sessionData;
    }

    private SessionForCatalogRecord buildSession() {
        SessionForCatalogRecord record = new SessionForCatalogRecord();
        record.setIdsesion(SESSION_ID);
        record.setIdevento(EVENT_ID);
        record.setEstado(3);
        record.setPublicado((byte) 1);
        record.setIspreview(false);
        record.setFechainiciosesion(new Timestamp(new Date().getTime()));
        record.setFechapublicacion(new Timestamp(new Date().getTime()));
        record.setIdpromotor(1);
        return record;
    }

    private CpanelEventoRecord buildEventRecord() {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setIdevento(EVENT_ID);
        event.setEstado(EventStatus.READY.getId());
        return event;
    }
} 
