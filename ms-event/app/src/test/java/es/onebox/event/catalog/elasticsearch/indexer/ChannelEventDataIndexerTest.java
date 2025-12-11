package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.dao.CatalogChannelEventCouchDao;
import es.onebox.event.catalog.dao.CatalogChannelSessionCouchDao;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDatesWithTimeZones;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventPostBookingQuestions;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.communicationelements.dao.EmailCommunicationElementDao;
import es.onebox.event.datasources.ms.channel.repository.ChannelsRepository;
import es.onebox.event.events.dao.ChannelEventCommunicationElementDao;
import es.onebox.event.events.dao.EventChannelCommElemDao;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.domain.eventconfig.PostBookingQuestionsConfig;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.postbookingquestions.dao.EventChannelPostBookingQuestionDao;
import es.onebox.event.events.postbookingquestions.dao.EventPostBookingQuestionDao;
import es.onebox.event.events.postbookingquestions.dao.PostBookingQuestionCouchDao;
import es.onebox.event.events.postbookingquestions.dao.record.EventPostBookingQuestionRecord;
import es.onebox.event.events.postbookingquestions.enums.EventChannelsPBQType;
import es.onebox.event.events.postbookingquestions.enums.PostBookingQuestionType;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.taxonomy.dao.CustomTaxonomyDao;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChannelEventDataIndexerTest {

    private static final Integer EVENT_ID = 877;
    private static final Integer CHANNEL_ID = 456;
    private static final Integer SESSION_ID = 123;

    @Mock
    private ChannelEventElasticDao channelEventElasticDao;
    @Mock
    private ChannelSessionElasticDao channelSessionElasticDao;
    @Mock
    private CatalogChannelEventCouchDao catalogChannelEventCouchDao;
    @Mock
    private CustomTaxonomyDao customTaxonomyDao;
    @Mock
    private EmailCommunicationElementDao emailCommunicationElementDao;
    @Mock
    private CacheRepository localCacheRepository;
    @Mock
    private EventChannelCommElemDao eventChannelCommElemDao;
    @Mock
    private CatalogChannelSessionCouchDao catalogChannelSessionCouchDao;
    @Mock
    private ChannelsRepository channelsRepository;
    @Mock
    private ChannelEventCommunicationElementDao channelEventCommunicationElementDao;
    @Mock
    private EventPostBookingQuestionDao eventPostBookingQuestionDao;
    @Mock
    private EventChannelPostBookingQuestionDao eventChannelPostBookingQuestionDao;
    @Mock
    private PostBookingQuestionCouchDao postBookingQuestionCouchDao;

    private ChannelEventDataIndexer channelEventDataIndexer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.channelEventDataIndexer = new ChannelEventDataIndexer(
                channelEventElasticDao,
                channelSessionElasticDao,
                catalogChannelEventCouchDao,
                customTaxonomyDao,
                emailCommunicationElementDao,
                eventChannelCommElemDao,
                catalogChannelSessionCouchDao,
                channelEventCommunicationElementDao,
                eventPostBookingQuestionDao,
                eventChannelPostBookingQuestionDao,
                channelsRepository,
                localCacheRepository,
                postBookingQuestionCouchDao
        );
    }

    @Test
    void testIndexChannelEvents_whenPartialBasic_shouldUpdateBasicInfo() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_BASIC);

        when(channelEventElasticDao.getByEventId(anyLong())).thenReturn(new ArrayList<>());

        ChannelEventData oldValue = buildChannelEventData();
        when(catalogChannelEventCouchDao.get(anyString(), anyString())).thenReturn(oldValue.getChannelEvent());

        Date currentDate = new Date();
        Assertions.assertTrue(oldValue.getChannelEvent().getCatalogInfo().getDate().getStart().before(currentDate));

        when(catalogChannelSessionCouchDao.bulkGet(any())).thenReturn(List.of(buildChannelSessionData().getChannelSession()));

        channelEventDataIndexer.indexChannelEvents(context);

        verify(catalogChannelEventCouchDao, times(1)).bulkUpsert(anyList());
        verify(channelEventElasticDao, times(1)).bulkUpsert(anyBoolean(), anyString(),
                argThat((ArgumentMatcher<ChannelEventData>) argument ->
                {
                    ChannelEvent newValue = argument.getChannelEvent();
                    return argument.getMustBeIndexed().equals(true) &&
                            newValue.getCatalogInfo() != null &&
                            newValue.getCatalogInfo().getDate().getStart().after(currentDate);
                }));
    }

    @Test
    void testIndexChannelEvent_whenPostBookingQuestionsExistsTypeLIST_shouldReturnPostBookingQuestions() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_BASIC);
        EventConfig eventConfig = buildEventConfigWithPostBookingQuestions(true, EventChannelsPBQType.LIST);
        context.setEventConfig(eventConfig);

        when(eventChannelPostBookingQuestionDao.getEventChannelsPostBookingQuestions(EVENT_ID, CHANNEL_ID))
                .thenReturn(buildEventChannelPostBookingQuestions(3));
        when(postBookingQuestionCouchDao.bulkGet(Mockito.<List<String>>any()))
                .thenReturn(buildPostBookingQuestionsDocs(3));

        ChannelEventPostBookingQuestions postBookingQuestions =
                channelEventDataIndexer.getChannelEventPostBookingQuestions(context, CHANNEL_ID.longValue());

        verify(eventPostBookingQuestionDao, never()).getEventPostBookingQuestions(anyInt());

        Assertions.assertNotNull(postBookingQuestions);
        Assertions.assertEquals(3, postBookingQuestions.getQuestions().size());
    }

    @Test
    void testIndexChannelEvent_whenPostBookingQuestionsExistsTypeALL_shouldReturnPostBookingQuestions() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_BASIC);
            EventConfig eventConfig = buildEventConfigWithPostBookingQuestions(true, EventChannelsPBQType.ALL);
        context.setEventConfig(eventConfig);

        when(eventPostBookingQuestionDao.getEventPostBookingQuestions(EVENT_ID))
                .thenReturn(buildEventChannelPostBookingQuestions(5));
        when(postBookingQuestionCouchDao.bulkGet(Mockito.<List<String>>any()))
                .thenReturn(buildPostBookingQuestionsDocs(5));

        ChannelEventPostBookingQuestions postBookingQuestions =
                channelEventDataIndexer.getChannelEventPostBookingQuestions(context, CHANNEL_ID.longValue());

        verify(eventChannelPostBookingQuestionDao, never()).getEventChannelsPostBookingQuestions(anyInt(), anyInt());

        Assertions.assertNotNull(postBookingQuestions);
        Assertions.assertEquals(5, postBookingQuestions.getQuestions().size());
    }

    @Test
    void testIndexChannelEvent_whenPostBookingQuestionsEmpty_shouldNotReturnPostBookingQuestions() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_BASIC);
        EventConfig eventConfig = buildEventConfigWithPostBookingQuestions(true, EventChannelsPBQType.ALL);
        context.setEventConfig(eventConfig);

        when(eventPostBookingQuestionDao.getEventPostBookingQuestions(EVENT_ID))
                .thenReturn(buildEventChannelPostBookingQuestions(0));
        when(postBookingQuestionCouchDao.bulkGet(Mockito.<List<String>>any()))
                .thenReturn(buildPostBookingQuestionsDocs(0));

        ChannelEventPostBookingQuestions postBookingQuestions =
                channelEventDataIndexer.getChannelEventPostBookingQuestions(context, CHANNEL_ID.longValue());
        Assertions.assertNull(postBookingQuestions);
    }

    @Test
    void testIndexChannelEvent_whenPostBookingQuestionsNotExists_shouldNotReturnPostBookingQuestions() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_BASIC);
        EventConfig eventConfig = buildEventConfigWithPostBookingQuestions(true, EventChannelsPBQType.LIST);
        context.setEventConfig(eventConfig);

        when(eventChannelPostBookingQuestionDao.getEventChannelsPostBookingQuestions(EVENT_ID, CHANNEL_ID))
                .thenReturn(buildEventChannelPostBookingQuestions(0));
        when(postBookingQuestionCouchDao.bulkGet(Mockito.<List<String>>any()))
                .thenReturn(buildPostBookingQuestionsDocs(0));

        ChannelEventPostBookingQuestions postBookingQuestions =
                channelEventDataIndexer.getChannelEventPostBookingQuestions(context, CHANNEL_ID.longValue());

        verify(eventPostBookingQuestionDao, never()).getEventPostBookingQuestions(anyInt());

        Assertions.assertNull(postBookingQuestions);
    }

    @Test
    void testIndexChannelEvent_whenPostBookingQuestionsNotEnabled_shouldNotReturnPostBookingQuestions() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_BASIC);
        EventConfig eventConfig = buildEventConfigWithPostBookingQuestions(false, EventChannelsPBQType.LIST);
        context.setEventConfig(eventConfig);

        ChannelEventPostBookingQuestions postBookingQuestions =
                channelEventDataIndexer.getChannelEventPostBookingQuestions(context, CHANNEL_ID.longValue());

        verify(eventChannelPostBookingQuestionDao, never()).getEventChannelsPostBookingQuestions(anyInt(), anyInt());
        verify(eventPostBookingQuestionDao, never()).getEventPostBookingQuestions(anyInt());
        Assertions.assertNull(postBookingQuestions);
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

        ctx.addDocumentIndexed(buildChannelSessionData());
        ctx.setSessions(List.of(buildSession()));
        ctx.setAllSessions(ctx.getSessions());

        return ctx;
    }

    private ChannelEventData buildChannelEventData() {
        ChannelEventData channelEvent = new ChannelEventData();
        channelEvent.setMustBeIndexed(true);
        channelEvent.setChannelEvent(buildChannelEvent());
        return channelEvent;
    }


    private static ChannelEvent buildChannelEvent() {
        ChannelEvent channelEvent = new ChannelEvent();
        channelEvent.setChannelId(CHANNEL_ID.longValue());
        channelEvent.setEventId(EVENT_ID.longValue());
        ChannelCatalogEventInfo catalogInfo = new ChannelCatalogEventInfo();
        catalogInfo.setDate(new ChannelCatalogDatesWithTimeZones());
        catalogInfo.getDate().setStart(new Date(ZonedDateTime.now().minusHours(1L).toInstant().toEpochMilli()));
        channelEvent.setCatalogInfo(catalogInfo);
        return channelEvent;
    }

    private ChannelSessionData buildChannelSessionData() {
        ChannelSessionData sessionData = new ChannelSessionData();
        ChannelSession channelSession = new ChannelSession();
        channelSession.setSessionId(SESSION_ID.longValue());
        channelSession.setChannelId(CHANNEL_ID.longValue());
        channelSession.setForSale(true);
        ChannelCatalogDatesWithTimeZones date = new ChannelCatalogDatesWithTimeZones();
        date.setStart(new Date(ZonedDateTime.now().plusHours(1L).toInstant().toEpochMilli()));
        channelSession.setDate(date);
        sessionData.setChannelSession(channelSession);
        return sessionData;
    }

    private SessionForCatalogRecord buildSession() {
        SessionForCatalogRecord record = new SessionForCatalogRecord();
        record.setIdsesion(SESSION_ID);
        record.setIdevento(EVENT_ID);
        record.setEstado(3);
        record.setPublicado((byte) 1);
        record.setIspreview(false);
        record.setFechainiciosesion(new Timestamp(ZonedDateTime.now().plusHours(1L).toInstant().toEpochMilli()));
        record.setFechapublicacion(new Timestamp(new Date().getTime()));
        return record;
    }

    private CpanelEventoRecord buildEventRecord() {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setIdevento(EVENT_ID);
        event.setEstado(EventStatus.READY.getId());
        return event;
    }

    private EventConfig buildEventConfigWithPostBookingQuestions(boolean enabled, EventChannelsPBQType type) {
        PostBookingQuestionsConfig pbqConfig = new PostBookingQuestionsConfig();
        pbqConfig.setEnabled(enabled);
        pbqConfig.setType(type);

        EventConfig eventConfig = new EventConfig();
        eventConfig.setEventId(EVENT_ID);
        eventConfig.setPostBookingQuestionsConfig(pbqConfig);
        return eventConfig;
    }

    private List<EventPostBookingQuestionRecord> buildEventChannelPostBookingQuestions(int size) {
        ArrayList<EventPostBookingQuestionRecord> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            EventPostBookingQuestionRecord record = new EventPostBookingQuestionRecord();
            record.setIdExterno(UUID.randomUUID().toString());
            list.add(record);
        }
        return list;
    }

    private List<es.onebox.event.events.postbookingquestions.domain.PostBookingQuestion> buildPostBookingQuestionsDocs(int size) {
        ArrayList<es.onebox.event.events.postbookingquestions.domain.PostBookingQuestion> list = new ArrayList<>();
        if (size == 0) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            es.onebox.event.events.postbookingquestions.domain.PostBookingQuestion question = new es.onebox.event.events.postbookingquestions.domain.PostBookingQuestion();
            question.setId(UUID.randomUUID().toString());
            question.setType(PostBookingQuestionType.NUMBER);
            list.add(question);
        }
        return list;
    }
} 
