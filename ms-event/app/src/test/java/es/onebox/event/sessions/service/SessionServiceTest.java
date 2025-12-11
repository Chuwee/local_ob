package es.onebox.event.sessions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.scheduler.TaskInfo;
import es.onebox.core.scheduler.TaskService;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.request.RelativeDateTime;
import es.onebox.core.serializer.dto.request.UnaryOperator;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.attendants.AttendantsConfigService;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.amqp.channelsuggestionscleanup.ChannelSuggestionsCleanUpService;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.request.PriceTypeBaseFilter;
import es.onebox.event.datasources.integration.dispatcher.repository.IntDispatcherRepository;
import es.onebox.event.datasources.ms.entity.dto.ProducerDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.datasources.ms.ticket.repository.SessionRepository;
import es.onebox.event.datasources.ms.venue.dto.Gate;
import es.onebox.event.datasources.ms.venue.dto.Venue;
import es.onebox.event.datasources.ms.venue.dto.VenueTemplate;
import es.onebox.event.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.events.dao.EventAvetConfigCouchDao;
import es.onebox.event.events.dao.EventCommunicationElementDao;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.dao.RateGroupDao;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.domain.eventconfig.EventAvetConfig;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.dto.TimeZoneDTO;
import es.onebox.event.events.enums.BookingExpirationType;
import es.onebox.event.events.enums.BookingSessionExpiration;
import es.onebox.event.events.enums.BookingSessionTimespan;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.events.enums.SessionPackType;
import es.onebox.event.events.service.EventExternalBarcodeConfigService;
import es.onebox.event.exception.MsEventRateErrorCode;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.loyaltypoints.sessions.dao.SessionLoyaltyPointsConfigCouchDao;
import es.onebox.event.secondarymarket.domain.SessionSecondaryMarketDates;
import es.onebox.event.secondarymarket.service.SecondaryMarketService;
import es.onebox.event.secondarymarket.service.SessionSecondaryMarketConfigService;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.amqp.avetavailability.AvetAvailabilityMatchScheduleService;
import es.onebox.event.sessions.amqp.seatgeneration.GenerateSeatService;
import es.onebox.event.sessions.amqp.seatremove.SeatRemoveService;
import es.onebox.event.sessions.amqp.sessionclone.SessionCloneService;
import es.onebox.event.sessions.dao.PriceTypeLabelSessionDao;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.SessionRateDao;
import es.onebox.event.sessions.dao.SessionTaxesDao;
import es.onebox.event.sessions.dao.TaxDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dao.record.ZonaPreciosConfigRecord;
import es.onebox.event.sessions.domain.Session;
import es.onebox.event.sessions.domain.sessionconfig.QueueItConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.domain.sessionconfig.StreamingVendorConfig;
import es.onebox.event.sessions.dto.CloneSessionDTO;
import es.onebox.event.sessions.dto.CreateSessionDTO;
import es.onebox.event.sessions.dto.DeleteSessionDTO;
import es.onebox.event.sessions.dto.PriceTypeRequestDTO;
import es.onebox.event.sessions.dto.PriceTypesDTO;
import es.onebox.event.sessions.dto.RateDTO;
import es.onebox.event.sessions.dto.SessionDateDTO;
import es.onebox.event.sessions.dto.SessionGenerationStatus;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.dto.SessionStreamingDTO;
import es.onebox.event.sessions.dto.SessionsDTO;
import es.onebox.event.sessions.dto.StreamingVendor;
import es.onebox.event.sessions.dto.UpdateSessionRequestDTO;
import es.onebox.event.sessions.dto.UpdateSessionsRequestDTO;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.sorting.SessionField;
import es.onebox.event.venues.dao.BlockingReasonDao;
import es.onebox.event.venues.dao.EntityVenueTemplateDao;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEntidadRecintoConfigRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEntidadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRazonBloqueoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionTarifaRecord;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static es.onebox.event.exception.MsEventSessionErrorCode.SALE_TYPE_MANDATORY;
import static es.onebox.utils.ObjectRandomizer.random;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class SessionServiceTest {

    @Mock
    private SeasonSessionDao seasonSessionDao;
    @Mock
    private SessionRateDao sessionRateDao;
    @Mock
    private EventDao eventDao;
    @Mock
    private EntityDao entityDao;
    @Mock
    private EventConfigCouchDao eventConfigCouchDao;
    @Mock
    private PriceTypeConfigDao priceZoneConfigDao;
    @Mock
    private PriceTypeLabelSessionDao priceTypeLabelSessionDao;
    @Mock
    private RateDao rateDao;
    @Mock
    private RateGroupDao rateGroupDao;
    @Mock
    private SessionDao sessionDao;
    @Mock
    private EntityVenueTemplateDao entityVenueTemplateDao;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private GenerateSeatService generateSeatService;
    @Mock
    private RefreshDataService refreshDataService;
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private AttendantsConfigService attendantsConfigService;
    @Mock
    private SessionCloneService sessionCloneService;
    @Mock
    private SessionConfigCouchDao sessionConfigCouchDao;
    @Mock
    private EventAvetConfigCouchDao eventAvetConfigCouchDao;
    @Mock
    private SeatRemoveService seatRemoveService;
    @Mock
    private AvetAvailabilityMatchScheduleService avetAvailabilityMatchScheduleService;
    @Mock
    private BlockingReasonDao blockingReasonDao;
    @Mock
    private EventCommunicationElementDao communicationElementDao;
    @Mock
    private StaticDataContainer staticDataContainer;
    @Mock
    private TaxDao taxDao;
    @Mock
    private TaskService taskService;
    @Mock
    private SessionCommunicationElementsService sessionCommunicationElementService;
    @Mock
    private VenuesRepository venuesRepository;
    @Mock
    private SessionValidationHelper sessionValidationHelper;
    @InjectMocks
    private SessionService sessionService;
    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private EventExternalBarcodeConfigService eventExternalBarcodeConfigService;
    @Mock
    private SessionExternalBarcodeConfigService sessionExternalBarcodeConfigService;
    @Mock
    private WebhookService webhookService;
    @Mock
    private SessionSecondaryMarketConfigService secondaryMarketConfigService;
    @Mock
    private SessionLoyaltyPointsConfigCouchDao sessionLoyaltyPointsConfigCouchDao;
    @Mock
    private SecondaryMarketService secondaryMarketService;
    @Mock
    private SessionTaxesDao sessionTaxesDao;
    @Mock
    private IntDispatcherRepository intDispatcherRepository;
    @Mock
    private ChannelSuggestionsCleanUpService channelSuggestionsCleanUpService;

    private List<CreateSessionDTO> sessions;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        sessions = new ArrayList<>();
        when(secondaryMarketService.getAllowSecondaryMarket(anyInt())).thenReturn(false);
    }

    @Test
    void eventIdMustBeValid() {
        Assertions.assertThrows(OneboxRestException.class, () -> sessionService.createSessions(-1L, sessions));
    }

    @Test
    void sessionsMustBeValid() {
        Assertions.assertThrows(OneboxRestException.class, () -> sessionService.createSessions(-1L, sessions));
    }

    @Test
    void bulkCreateLimit() {

        List<CreateSessionDTO> sessions = new ArrayList<>();
        for (int i = 0; i < 1001; i++) {
            sessions.add(initSessionDTO());
        }

        try {
            sessionService.createSessions(1L, sessions);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals("Bulk create maximum limit exceeded: 1000", e.getMessage());
        }
    }

    @Test
    void bulkCreate() {

        List<CreateSessionDTO> sessions = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            sessions.add(initSessionDTO());
        }

        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(eventDao.getEventVenueTemplate(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new CpanelConfigRecintoRecord());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());
        Mockito.when(entityVenueTemplateDao.getByVenueTemplateId(Mockito.anyLong())).thenReturn(initVenue());
        Mockito.when(sessionDao.bulkInsertSessions(Mockito.anyList()))
                .thenReturn(ObjectRandomizer.randomListOf(Long.class, sessions.size()));
        Mockito.when(taxDao.getEventTaxes(Mockito.any())).thenReturn(Collections.singletonList(1L));
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(new SessionConfig());

        List<Long> sessionIds = sessionService.createSessions(1L, sessions);

        Assertions.assertEquals(sessions.size(), sessionIds.size());
    }

    @Test
    void create_validateBasicDates() {

        CreateSessionDTO createSessionDTO = initSessionDTO();

        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(eventDao.getEventVenueTemplate(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new CpanelConfigRecintoRecord());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());
        Mockito.when(entityVenueTemplateDao.getByVenueTemplateId(Mockito.anyLong())).thenReturn(initVenue());
        Mockito.when(sessionDao.bulkInsertSessions(Mockito.anyList())).thenReturn(Collections.singletonList(1L));
        Mockito.when(taxDao.getEventTaxes(Mockito.any())).thenReturn(Collections.singletonList(1L));
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(new SessionConfig());

        Long sessionId = sessionService.createSession(1L, createSessionDTO);

        Assertions.assertEquals(Long.valueOf(1), sessionId);
    }

    @Test
    void create_external() {
        CreateSessionDTO createSessionDTO = initSessionDTO();
        EventConfig cfg = new EventConfig();
        cfg.setInventoryProvider(Provider.SEETICKETS);
        ArgumentCaptor<List<Session>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(eventDao.getEventVenueTemplate(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new CpanelConfigRecintoRecord());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());
        Mockito.when(entityVenueTemplateDao.getByVenueTemplateId(Mockito.anyLong())).thenReturn(initVenue());
        Mockito.when(sessionDao.bulkInsertSessions(argumentCaptor.capture())).thenReturn(Collections.singletonList(1L));
        Mockito.when(taxDao.getEventTaxes(Mockito.any())).thenReturn(Collections.singletonList(1L));
        Mockito.when(eventConfigCouchDao.get("1")).thenReturn(cfg);
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(new SessionConfig());
        Long sessionId = sessionService.createSession(1L, createSessionDTO);
        Assertions.assertEquals(Long.valueOf(1), sessionId);
        Assertions.assertTrue(argumentCaptor.getValue().get(0).getExternal());
    }

    @Test
    void create_checkReleaseConstraint() {

        CreateSessionDTO createSessionDTO = initSessionDTO();

        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(eventDao.getEventVenueTemplate(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new CpanelConfigRecintoRecord());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());
        Mockito.when(entityVenueTemplateDao.getByVenueTemplateId(Mockito.anyLong())).thenReturn(initVenue());
        Mockito.when(sessionDao.bulkInsertSessions(Mockito.anyList())).thenReturn(Collections.singletonList(1L));
        Mockito.when(taxDao.getEventTaxes(Mockito.any())).thenReturn(Collections.singletonList(1L));

        createSessionDTO.setPublishDate(null);

        try {
            sessionService.createSession(1L, createSessionDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.INVALID_SESSION_DATES_RELEASE_REQUIRED.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void create_checkSalesConstraint() {

        CreateSessionDTO createSessionDTO = initSessionDTO();

        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(eventDao.getEventVenueTemplate(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new CpanelConfigRecintoRecord());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());
        Mockito.when(entityVenueTemplateDao.getByVenueTemplateId(Mockito.anyLong())).thenReturn(initVenue());
        Mockito.when(sessionDao.bulkInsertSessions(Mockito.anyList())).thenReturn(Collections.singletonList(1L));
        Mockito.when(taxDao.getEventTaxes(Mockito.any())).thenReturn(Collections.singletonList(1L));

        createSessionDTO.setSalesStartDate(null);

        try {
            sessionService.createSession(1L, createSessionDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.INVALID_SESSION_DATES_SALES_START_REQUIRED.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void create_checkActivitySalesType() {

        CpanelEventoRecord event = initEvent();
        event.setTipoevento(EventType.ACTIVITY.getId());
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(event);
        Mockito.when(eventDao.getEventVenueTemplate(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new CpanelConfigRecintoRecord());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());
        Mockito.when(taxDao.getEventTaxes(Mockito.any())).thenReturn(Collections.singletonList(1L));

        CreateSessionDTO createSessionDTO = initSessionDTO();
        try {
            sessionService.createSession(1L, createSessionDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(SALE_TYPE_MANDATORY.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void create_checkBookingDateConstraint() {

        CreateSessionDTO createSessionDTO = initSessionDTO();

        CpanelEventoRecord event = initEvent();
        event.setPermitereservas((byte) 1);
        event.setTipofechalimitereserva(BookingExpirationType.SESSION.getTipo());
        event.setTipounidadeslimite(BookingSessionTimespan.HOUR.getTipo());
        event.setTipolimite(BookingSessionExpiration.BEFORE.getTipo());
        event.setNumunidadeslimite(1);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(event);
        Mockito.when(eventDao.getEventVenueTemplate(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new CpanelConfigRecintoRecord());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());
        Mockito.when(entityVenueTemplateDao.getByVenueTemplateId(Mockito.anyLong())).thenReturn(initVenue());
        Mockito.when(sessionDao.bulkInsertSessions(Mockito.anyList())).thenReturn(Collections.singletonList(1L));
        Mockito.when(taxDao.getEventTaxes(Mockito.any())).thenReturn(Collections.singletonList(1L));

        createSessionDTO.setBookingStartDate(ZonedDateTime.now());
        createSessionDTO.setBookingEndDate(null);

        try {
            sessionService.createSession(1L, createSessionDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.INVALID_SESSION_DATES_BOOKING_END_REQUIRED.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void create_avetSocketSession() {

        Long eventId = 1L;
        Long sessionId = 1L;
        Integer matchId = 1111;

        CreateSessionDTO session = initSessionDTO();

        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent(EventType.AVET));
        Mockito.when(eventDao.getEventVenueTemplate(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new CpanelConfigRecintoRecord());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());
        Mockito.when(rateDao.getRatesByDefaultSessionId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(initRate());
        Mockito.when(entityVenueTemplateDao.getByVenueTemplateId(Mockito.anyLong())).thenReturn(initVenue());
        Mockito.when(sessionDao.bulkInsertSessions(Mockito.anyList())).thenReturn(Arrays.asList(sessionId));
        SessionRecord sessionRecord = initSession(sessionId.intValue(), matchId);
        Mockito.when(sessionDao.findSession(Mockito.any())).thenReturn(sessionRecord);
        Mockito.when(eventAvetConfigCouchDao.get(Mockito.any())).thenReturn(initEventAvetConfig(true));
        Mockito.when(taxDao.getEventTaxes(Mockito.any())).thenReturn(Collections.singletonList(1L));
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(new SessionConfig());

        Long createdSessionId = sessionService.createSession(eventId, session);

        Assertions.assertEquals(sessionId, createdSessionId);
        Mockito.verify(avetAvailabilityMatchScheduleService, times(1)).createAvetAvailabilitySchedule(eq(matchId),
                eq(sessionId.intValue()));
    }

    @Test
    void create_sessionPack() {
        CreateSessionDTO createSessionDTO = initSessionDTO();

        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());
        Mockito.when(entityVenueTemplateDao.getByVenueTemplateId(Mockito.anyLong())).thenReturn(initVenue());
        Mockito.when(sessionDao.bulkInsertSessions(Mockito.anyList())).thenReturn(Collections.singletonList(1L));
        Mockito.when(taxDao.getEventTaxes(Mockito.any())).thenReturn(Collections.singletonList(1L));
        CpanelConfigRecintoRecord vt = new CpanelConfigRecintoRecord();
        vt.setIdconfiguracion(1);
        Mockito.when(eventDao.getEventVenueTemplate(Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(vt);
        SessionRecord s2 = initSession(2);
        s2.setEstado(2);
        SessionRecord s3 = initSession(3);
        s3.setEstado(2);
        Mockito.when(sessionDao.findFlatSessions(Mockito.any())).thenReturn(Arrays.asList(s2, s3));
        CpanelRazonBloqueoRecord br = new CpanelRazonBloqueoRecord();
        br.setIdconfiguracion(1);
        Mockito.when(blockingReasonDao.findById(Mockito.any())).thenReturn(br);
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(new SessionConfig());

        createSessionDTO.setSeasonPass(true);
        createSessionDTO.setSeasonSessions(Arrays.asList(2L, 3L));
        final HashMap<Integer, Integer> seasonPassBlockingActions = new HashMap<>();
        seasonPassBlockingActions.put(1, 1);
        createSessionDTO.setSeasonPassBlockingActions(seasonPassBlockingActions);
        Long sessionId = sessionService.createSession(1L, createSessionDTO);

        Assertions.assertEquals(Long.valueOf(1), sessionId);
    }

    @Test
    void create_sessionPack_invalidRelatedStatus() {
        CreateSessionDTO createSessionDTO = initSessionDTO();

        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(eventDao.getEventVenueTemplate(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new CpanelConfigRecintoRecord());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());
        Mockito.when(entityVenueTemplateDao.getByVenueTemplateId(Mockito.anyLong())).thenReturn(initVenue());
        Mockito.when(sessionDao.bulkInsertSessions(Mockito.anyList())).thenReturn(Collections.singletonList(1L));
        Mockito.when(taxDao.getEventTaxes(Mockito.any())).thenReturn(Collections.singletonList(1L));
        SessionRecord s2 = initSession(2);
        s2.setEstado(3);
        Mockito.when(sessionDao.findFlatSessions(Mockito.any())).thenReturn(Arrays.asList(s2));
        CpanelRazonBloqueoRecord br = new CpanelRazonBloqueoRecord();
        br.setIdconfiguracion(1);
        Mockito.when(blockingReasonDao.findById(Mockito.any())).thenReturn(br);

        createSessionDTO.setSeasonPass(true);
        createSessionDTO.setSeasonSessions(Arrays.asList(2L, 3L));

        try {
            sessionService.createSession(1L, createSessionDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.SESSION_CREATE_PACK_STATUS.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void create_sessionPack_invalidPartialRefund() {
        CreateSessionDTO createSessionDTO = initSessionDTO();
        createSessionDTO.setAllowPartialRefund(true);
        CpanelEventoRecord event = initEvent();
        event.setTipoabono(SessionPackType.RESTRICTED.getId());
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(event);
        Mockito.when(eventDao.getEventVenueTemplate(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(new CpanelConfigRecintoRecord());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());
        Mockito.when(entityVenueTemplateDao.getByVenueTemplateId(Mockito.anyLong())).thenReturn(initVenue());
        Mockito.when(sessionDao.bulkInsertSessions(Mockito.anyList())).thenReturn(Collections.singletonList(1L));
        Mockito.when(taxDao.getEventTaxes(Mockito.any())).thenReturn(Collections.singletonList(1L));
        SessionRecord s2 = initSession(2);
        SessionRecord s3 = initSession(3);
        s2.setEstado(SessionStatus.SCHEDULED.getId());
        s3.setEstado(SessionStatus.SCHEDULED.getId());
        Mockito.when(sessionDao.findFlatSessions(Mockito.any())).thenReturn(Arrays.asList(s2, s3));
        CpanelRazonBloqueoRecord br = new CpanelRazonBloqueoRecord();
        br.setIdconfiguracion(1);
        Mockito.when(blockingReasonDao.findById(Mockito.any())).thenReturn(br);

        createSessionDTO.setSeasonPass(true);
        createSessionDTO.setSeasonSessions(Arrays.asList(2L, 3L));

        try {
            sessionService.createSession(1L, createSessionDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.SESSION_CREATE_PACK_ALLOW_PARTIAL_REFUND.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void postCreate_enqueue() {
        Long sessionId = 10L;
        Long eventId = 1L;

        CpanelEventoRecord eventRecord = initEvent();
        eventRecord.setIdevento(eventId.intValue());
        eventRecord.setIdentidad(1);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(eventRecord);

        CreateSessionDTO createData = new CreateSessionDTO();
        createData.setSeasonPass(false);
        sessionService.postCreateSession(eventId, sessionId, createData);

        Mockito.verify(sessionRepository, times(1)).createSession(eq(sessionId), eq(eventId), anyLong());
        Mockito.verify(generateSeatService, times(1)).generateSeats(eq(sessionId), anyBoolean(), any(), anyBoolean());
        Mockito.verify(refreshDataService, times(1)).refreshEvent(eq(eventId), anyString());
    }

    @Test
    void postClone_enqueue() {
        Long sourceSessionId = 10L;
        Long eventId = 1L;

        CpanelEventoRecord eventRecord = initEvent();
        eventRecord.setIdevento(eventId.intValue());
        eventRecord.setIdentidad(1);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(eventRecord);

        CloneSessionDTO cloneData = new CloneSessionDTO();
        cloneData.setSourceSessionId(1L);
        cloneData.setTargetBlockingReasonId(1L);

        sessionService.postCloneSession(eventId, sourceSessionId, cloneData);

        Mockito.verify(sessionRepository, times(1)).createSession(eq(sourceSessionId), eq(eventId), anyLong());
        Mockito.verify(sessionCloneService, times(1)).cloneSession(anyLong(), eq(sourceSessionId), any());
        Mockito.verify(generateSeatService, times(0)).generateSeats(eq(sourceSessionId), anyBoolean(), any(), anyBoolean());
        Mockito.verify(refreshDataService, times(1)).refreshEvent(eq(eventId), anyString());
    }

    @Test
    void postClone_enqueueActivity() {
        Long sourceSessionId = 10L;
        Long eventId = 1L;

        CpanelEventoRecord eventRecord = initEvent();
        eventRecord.setIdevento(eventId.intValue());
        eventRecord.setIdentidad(1);
        eventRecord.setTipoevento(EventType.ACTIVITY.getId());
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(eventRecord);

        CloneSessionDTO cloneData = new CloneSessionDTO();
        cloneData.setSourceSessionId(1L);
        cloneData.setTargetBlockingReasonId(1L);

        sessionService.postCloneSession(eventId, sourceSessionId, cloneData);

        Mockito.verify(sessionRepository, times(1)).createSession(eq(sourceSessionId), eq(eventId), anyLong());
        Mockito.verify(sessionCloneService, times(0)).cloneSession(anyLong(), eq(sourceSessionId), any());
        Mockito.verify(generateSeatService, times(1)).generateSeats(eq(sourceSessionId), anyBoolean(), any(), anyBoolean());
        Mockito.verify(refreshDataService, times(1)).refreshEvent(eq(eventId), anyString());
    }

    @Test
    void clone_OK() {

        CloneSessionDTO cloneSessionDTO = new CloneSessionDTO();
        cloneSessionDTO.setName(ObjectRandomizer.randomString());
        cloneSessionDTO.setStartDate(ZonedDateTime.now());

        SessionRecord sessionRecord = initSession(1);
        sessionRecord.setEstadogeneracionaforo(SessionGenerationStatus.ACTIVE.getId());
        sessionRecord.setEsabono((byte) 0);
        sessionRecord.setReservasactivas((byte) 1);
        sessionRecord.setFechainicioreserva(new Timestamp(Instant.now().toEpochMilli()));
        sessionRecord.setFechafinreserva(new Timestamp(Instant.now().plusSeconds(60).toEpochMilli()));
        sessionRecord.setFechaventa(new Timestamp(Instant.now().plusSeconds(120).toEpochMilli()));
        Mockito.when(sessionValidationHelper.getSessionAndValidateWithEvent(anyLong(), anyLong())).thenReturn(sessionRecord);
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        eventRecord.setPermitereservas((byte) 1);
        Mockito.when(eventDao.findById(Mockito.any())).thenReturn(eventRecord);
        SessionRecord clonedRecord = new SessionRecord();
        clonedRecord.setIdsesion(2);
        Mockito.when(sessionDao.insert(Mockito.any())).thenReturn(clonedRecord);

        Long sessionId = sessionService.cloneSession(1L, 1L, cloneSessionDTO);

        Assertions.assertEquals(Long.valueOf(clonedRecord.getIdsesion()), sessionId);
    }

    @Test
    void clone_checkGenerationState() {

        CloneSessionDTO cloneSessionDTO = new CloneSessionDTO();
        cloneSessionDTO.setName(ObjectRandomizer.randomString());
        cloneSessionDTO.setStartDate(ZonedDateTime.now());

        SessionRecord sessionRecord = initSession(1);
        sessionRecord.setEstadogeneracionaforo(SessionGenerationStatus.PENDING.getId());
        Mockito.when(sessionValidationHelper.getSessionAndValidateWithEvent(anyLong(), anyLong())).thenReturn(sessionRecord);

        try {
            sessionService.cloneSession(1L, 1L, cloneSessionDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.SESSION_CLONE_GENERATION_STATUS.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void clone_checkSeasonPack() {

        CloneSessionDTO cloneSessionDTO = new CloneSessionDTO();
        cloneSessionDTO.setName(ObjectRandomizer.randomString());
        cloneSessionDTO.setStartDate(ZonedDateTime.now());

        SessionRecord sessionRecord = initSession(1);
        sessionRecord.setEstadogeneracionaforo(SessionGenerationStatus.ACTIVE.getId());
        sessionRecord.setEsabono((byte) 1);
        Mockito.when(sessionValidationHelper.getSessionAndValidateWithEvent(anyLong(), anyLong())).thenReturn(sessionRecord);

        try {
            sessionService.cloneSession(1L, 1L, cloneSessionDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.SESSION_CLONE_SESSION_PACK.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void clone_checkBookingDatesDisabled() {
        CloneSessionDTO cloneSessionDTO = new CloneSessionDTO();
        cloneSessionDTO.setName(ObjectRandomizer.randomString());
        cloneSessionDTO.setStartDate(ZonedDateTime.now().plusSeconds(10));

        SessionRecord sessionRecord = initSession(1);
        sessionRecord.setEstadogeneracionaforo(SessionGenerationStatus.ACTIVE.getId());
        sessionRecord.setEsabono((byte) 0);
        sessionRecord.setReservasactivas((byte) 1);
        sessionRecord.setFechainiciosesion(new Timestamp(Instant.now().toEpochMilli()));
        sessionRecord.setFechafinsesion(new Timestamp(Instant.now().plusSeconds(30).toEpochMilli()));
        sessionRecord.setFechainicioreserva(new Timestamp(Instant.now().toEpochMilli()));
        sessionRecord.setFechafinreserva(new Timestamp(Instant.now().plusSeconds(25).toEpochMilli()));
        Mockito.when(sessionValidationHelper.getSessionAndValidateWithEvent(anyLong(), anyLong())).thenReturn(sessionRecord);

        CpanelEventoRecord eventRecord = initEvent();
        eventRecord.setPermitereservas((byte) 1);
        eventRecord.setTipofechalimitereserva(BookingExpirationType.DATE.getTipo());
        eventRecord.setFechalimite(new Timestamp(Instant.now().plusSeconds(30).toEpochMilli()));
        Mockito.when(eventDao.findById(Mockito.any())).thenReturn(eventRecord);

        SessionRecord clonedRecord = new SessionRecord();
        clonedRecord.setIdsesion(1);
        Mockito.when(sessionDao.insert(Mockito.any())).thenReturn(clonedRecord);

        // New offset +10, limit +30, end_bookings +20 (+30 bookings + offset > limit) -> disabled
        sessionService.cloneSession(1L, 1L, cloneSessionDTO);

        Mockito.doAnswer(a -> {
            CpanelSesionRecord record = (CpanelSesionRecord) a.getArguments()[0];
            Assertions.assertEquals(Byte.valueOf((byte) 0), record.getReservasactivas());
            return Void.class;
        }).when(sessionDao).insert(any());
    }

    @Test
    void clone_checkBookingDatesEnabled() {

        CloneSessionDTO cloneSessionDTO = new CloneSessionDTO();
        cloneSessionDTO.setName(ObjectRandomizer.randomString());
        cloneSessionDTO.setStartDate(ZonedDateTime.now().plusSeconds(10));

        SessionRecord sessionRecord = initSession(1);
        sessionRecord.setEstadogeneracionaforo(SessionGenerationStatus.ACTIVE.getId());
        sessionRecord.setEsabono((byte) 0);
        sessionRecord.setReservasactivas((byte) 1);
        sessionRecord.setFechainiciosesion(new Timestamp(Instant.now().toEpochMilli()));
        sessionRecord.setFechafinsesion(new Timestamp(Instant.now().plusSeconds(30).toEpochMilli()));
        sessionRecord.setFechainicioreserva(new Timestamp(Instant.now().toEpochMilli()));
        sessionRecord.setFechafinreserva(new Timestamp(Instant.now().plusSeconds(15).toEpochMilli()));
        Mockito.when(sessionValidationHelper.getSessionAndValidateWithEvent(anyLong(), anyLong())).thenReturn(sessionRecord);

        CpanelEventoRecord eventRecord = initEvent();
        eventRecord.setPermitereservas((byte) 1);
        eventRecord.setTipofechalimitereserva(BookingExpirationType.DATE.getTipo());
        eventRecord.setFechalimite(new Timestamp(Instant.now().plusSeconds(30).toEpochMilli()));
        Mockito.when(eventDao.findById(Mockito.any())).thenReturn(eventRecord);

        SessionRecord clonedRecord = new SessionRecord();
        clonedRecord.setIdsesion(1);
        Mockito.when(sessionDao.insert(Mockito.any())).thenReturn(clonedRecord);

        // New offset +10, limit +30, end_bookings +15 (+25 bookings + offset <= limit) -> enabled
        sessionService.cloneSession(1L, 1L, cloneSessionDTO);

        Mockito.doAnswer(a -> {
            CpanelSesionRecord record = (CpanelSesionRecord) a.getArguments()[0];
            Assertions.assertEquals(Byte.valueOf((byte) 1), record.getReservasactivas());
            return Void.class;
        }).when(sessionDao).insert(any());
    }

    @Test
    void update_validateBasicDates() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setId(1L);
        updateSessionDTO.setDate(new SessionDateDTO());
        updateSessionDTO.getDate().setStart(ZonedDateTime.now().plusMinutes(30));

        SessionRecord sessionRecord = initSession(1);
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(new SessionConfig());

        sessionService.updateSession(1L, updateSessionDTO);

    }

    @Test
    void update_sessionStreaming() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setId(1L);
        SessionStreamingDTO streaming = new SessionStreamingDTO();
        streaming.setEnabled(true);
        streaming.setValue(ObjectRandomizer.randomString());
        streaming.setVendor(StreamingVendor.CUSTOM);
        updateSessionDTO.setStreaming(streaming);

        SessionRecord sessionRecord = initSession(1);
        sessionRecord.setFechainiciosesion(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now().plusHours(1)));
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        SessionConfig sessionConfig = new SessionConfig();
        StreamingVendorConfig streamingVendorConfig = new StreamingVendorConfig();
        streamingVendorConfig.setEnabled(true);
        streamingVendorConfig.setEmailMinutesBeforeStart(15);
        sessionConfig.setStreamingVendorConfig(streamingVendorConfig);
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(sessionConfig);
        Mockito.when(taskService.get(Mockito.anyString(), Mockito.anyString())).thenReturn(new TaskInfo());
        Mockito.when(secondaryMarketConfigService.getSessionSecondaryMarketConfigSafely(anyInt(), any())).thenReturn(null);

        sessionService.updateSession(1L, updateSessionDTO);

        Mockito.verify(sessionConfigCouchDao, times(1)).upsert(any(), any());
        Mockito.verify(taskService, times(1)).edit(any());
        Mockito.verify(taskService, times(0)).addJob(any());
    }

    @Test
    void update_sessionStreamingAddJob() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setId(1L);
        SessionStreamingDTO streaming = new SessionStreamingDTO();
        streaming.setEnabled(true);
        streaming.setValue(ObjectRandomizer.randomString());
        streaming.setVendor(StreamingVendor.CUSTOM);
        updateSessionDTO.setStreaming(streaming);

        SessionRecord sessionRecord = initSession(1);
        sessionRecord.setFechainiciosesion(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now().plusHours(1)));
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        SessionConfig sessionConfig = new SessionConfig();
        StreamingVendorConfig streamingVendorConfig = new StreamingVendorConfig();
        streamingVendorConfig.setEnabled(false);
        sessionConfig.setStreamingVendorConfig(streamingVendorConfig);
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(sessionConfig);
        Mockito.when(taskService.get(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        sessionService.updateSession(1L, updateSessionDTO);

        Mockito.verify(sessionConfigCouchDao, times(1)).upsert(any(), any());
        Mockito.verify(taskService, times(1)).addJob(any());
        Mockito.verify(taskService, times(0)).edit(any());
    }

    @Test
    void update_sessionStreamingWithoutRefreshJob() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setId(1L);
        SessionStreamingDTO streaming = new SessionStreamingDTO();
        streaming.setEnabled(true);
        streaming.setValue(ObjectRandomizer.randomString());
        streaming.setVendor(StreamingVendor.CUSTOM);
        updateSessionDTO.setStreaming(streaming);

        SessionRecord sessionRecord = initSession(1);
        sessionRecord.setFechainiciosesion(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now().plusMinutes(10)));
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        SessionConfig sessionConfig = new SessionConfig();
        StreamingVendorConfig streamingVendorConfig = new StreamingVendorConfig();
        streamingVendorConfig.setEnabled(true);
        streamingVendorConfig.setEmailMinutesBeforeStart(15);
        sessionConfig.setStreamingVendorConfig(streamingVendorConfig);
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(sessionConfig);
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setNextFireTime(Date.from(ZonedDateTime.now().plusMinutes(10).toInstant()));
        Mockito.when(taskService.get(Mockito.anyString(), Mockito.anyString())).thenReturn(taskInfo);

        sessionService.updateSession(1L, updateSessionDTO);

        Mockito.verify(sessionConfigCouchDao, times(1)).upsert(any(), any());
        Mockito.verify(taskService, times(0)).edit(any());
        Mockito.verify(taskService, times(0)).addJob(any());
    }

    @Test
    void update_sessionStreamingDisabled() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setId(1L);
        SessionStreamingDTO streaming = new SessionStreamingDTO();
        streaming.setEnabled(false);
        streaming.setValue(ObjectRandomizer.randomString());
        streaming.setVendor(StreamingVendor.CUSTOM);
        updateSessionDTO.setStreaming(streaming);

        SessionRecord sessionRecord = initSession(1);
        sessionRecord.setFechainiciosesion(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now().plusMinutes(10)));
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        SessionConfig sessionConfig = new SessionConfig();
        StreamingVendorConfig streamingVendorConfig = new StreamingVendorConfig();
        streamingVendorConfig.setEnabled(true);
        streamingVendorConfig.setEmailMinutesBeforeStart(15);
        sessionConfig.setStreamingVendorConfig(streamingVendorConfig);
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(sessionConfig);

        sessionService.updateSession(1L, updateSessionDTO);

        Mockito.verify(sessionConfigCouchDao, times(1)).upsert(any(), any());
        Mockito.verify(taskService, times(1)).delete(anyString(), anyString());
        Mockito.verify(taskService, times(0)).addJob(any());
    }

    @Test
    void update_checkReleaseConstraint() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setDate(new SessionDateDTO());
        updateSessionDTO.setEnableChannels(true);
        updateSessionDTO.getDate().setSalesStart(ZonedDateTimeWithRelative.of(ZonedDateTime.now().minusMinutes(15)));
        updateSessionDTO.getDate().setChannelPublication(ZonedDateTimeWithRelative.of(ZonedDateTime.now().minusMinutes(10)));
        updateSessionDTO.setId(1L);

        SessionRecord sessionRecord = initSession(1);
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());

        try {
            sessionService.updateSession(1L, updateSessionDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.INVALID_SESSION_DATES_RELEASE_AFTER_SALES_START.getErrorCode(),
                    e.getErrorCode());
        }
    }

    @Test
    void update_checkSalesEndConstraint() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setDate(new SessionDateDTO());
        updateSessionDTO.setEnableSales(true);
        updateSessionDTO.getDate().setSalesEnd(ZonedDateTimeWithRelative.of(ZonedDateTime.now().minusMinutes(61)));
        updateSessionDTO.setId(1L);

        SessionRecord sessionRecord = initSession(1);
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());

        try {
            sessionService.updateSession(1L, updateSessionDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.INVALID_SESSION_DATES_SALES_START_NOT_BEFORE_SALES_END.getErrorCode(),
                    e.getErrorCode());
        }
    }

    @Test
    void update_checkBookingStartConstraint() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setDate(new SessionDateDTO());
        updateSessionDTO.setEnableBookings(true);
        updateSessionDTO.getDate().setBookingsStart(ZonedDateTimeWithRelative.of(ZonedDateTime.now()));
        updateSessionDTO.getDate().setBookingsEnd(ZonedDateTimeWithRelative.of(ZonedDateTime.now().minusMinutes(1)));
        updateSessionDTO.setId(1L);

        SessionRecord sessionRecord = initSession(1);
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        CpanelEventoRecord event = initEvent();
        event.setPermitereservas((byte) 1);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(event);

        try {
            sessionService.updateSession(1L, updateSessionDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.INVALID_SESSION_DATES_BOOKING_START_NOT_BEFORE_BOOKING_END.getErrorCode(),
                    e.getErrorCode());
        }
    }

    @Test
    void update_deleteSession() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setId(1L);
        updateSessionDTO.setStatus(SessionStatus.DELETED);

        SessionRecord sessionRecord = initSession(1);
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(ordersRepository.sessionOperations(Mockito.any())).thenReturn(Map.of());
        Mockito.when(seasonSessionDao.findSessionPacksBySessionId(anyLong())).thenReturn(new ArrayList<>());

        sessionService.updateSession(1L, updateSessionDTO);

        Mockito.verify(sessionDao, times(1)).update(any());
        Mockito.doAnswer(a -> {
            CpanelSesionRecord record = (CpanelSesionRecord) a.getArguments()[0];
            Assertions.assertEquals(Integer.valueOf(0), record.getEstado());
            return Void.class;
        }).when(sessionDao).update(any());
    }

    @Test
    void update_deleteSessionWithSales() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setId(1L);
        updateSessionDTO.setStatus(SessionStatus.DELETED);

        SessionRecord sessionRecord = initSession(1);
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(ordersRepository.sessionOperations(Mockito.any())).thenReturn(Map.of(1L, 1L));

        try {
            sessionService.updateSession(1L, updateSessionDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.SESSION_WITH_BOOKED_SEAT.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void update_deleteSessionPack() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setId(1L);
        updateSessionDTO.setStatus(SessionStatus.DELETED);
        DeleteSessionDTO deleteData = new DeleteSessionDTO();
        deleteData.setBlockingReasonId(1);
        updateSessionDTO.setDeleteData(deleteData);

        SessionRecord sessionRecord = initSession(1);
        sessionRecord.setEsabono((byte) 1);
        sessionRecord.setVenueTemplateId(1);
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        CpanelEventoRecord event = initEvent();
        event.setTipoabono((byte) 1);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(event);
        Mockito.when(ordersRepository.sessionOperations(Mockito.any())).thenReturn(Map.of());
        Mockito.when(seasonSessionDao.findSessionPacksBySessionId(anyLong())).thenReturn(new ArrayList<>());
        Mockito.when(entityVenueTemplateDao.getById(Mockito.any())).thenReturn(initVenue());
        CpanelRazonBloqueoRecord br = new CpanelRazonBloqueoRecord();
        br.setIdconfiguracion(1);
        Mockito.when(blockingReasonDao.findById(Mockito.any())).thenReturn(br);

        sessionService.updateSession(1L, updateSessionDTO);

        Mockito.verify(sessionDao, times(1)).update(any());
        Mockito.doAnswer(a -> {
            CpanelSesionRecord record = (CpanelSesionRecord) a.getArguments()[0];
            Assertions.assertEquals(Integer.valueOf(0), record.getEstado());
            return Void.class;
        }).when(sessionDao).update(any());
    }

    @Test
    void updateSessions_bulkWithRates() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setName("test-mod");
        updateSessionDTO.setRates(Collections.singletonList(new RateDTO(1L, true)));
        UpdateSessionsRequestDTO updateSessionsDTO = new UpdateSessionsRequestDTO();
        updateSessionsDTO.setIds(Arrays.asList(1L, 2L, 3L));
        updateSessionsDTO.setValue(updateSessionDTO);

        SessionRecord sessionRecord = initSession(1);
        Mockito.when(sessionDao.findSession(Mockito.any())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());

        sessionService.updateSessions(1L, updateSessionsDTO, false);

        Mockito.doAnswer(a -> {
            UpdateSessionRequestDTO request = (UpdateSessionRequestDTO) a.getArguments()[1];
            Assertions.assertEquals(updateSessionDTO.getName(), request.getName());
            return Void.class;
        }).when(sessionDao).bulkUpdateSessions(anyList(), any());
        Mockito.verify(sessionRateDao, times(1)).cleanRatesForSessionIds(anyList());
        Mockito.verify(sessionRateDao, times(1)).bulkInsertSessionRates(anyList());
    }

    @Test
    void updateSessions_bulkWithRatestest() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setName("test-mod");
        updateSessionDTO.setRates(Collections.singletonList(new RateDTO(1L, true)));
        UpdateSessionsRequestDTO updateSessionsDTO = new UpdateSessionsRequestDTO();
        updateSessionsDTO.setIds(Arrays.asList(1L, 2L, 3L));
        updateSessionsDTO.setValue(updateSessionDTO);

        SessionRecord sessionRecord = initSession(1);
        Mockito.when(sessionDao.findSession(Mockito.any())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());

        sessionService.updateSessions(1L, updateSessionsDTO, false);

        Mockito.doAnswer(a -> {
            UpdateSessionRequestDTO request = (UpdateSessionRequestDTO) a.getArguments()[1];
            Assertions.assertEquals(updateSessionDTO.getName(), request.getName());
            return Void.class;
        }).when(sessionDao).bulkUpdateSessions(anyList(), any());
        Mockito.verify(sessionRateDao, times(1)).cleanRatesForSessionIds(anyList());
        Mockito.verify(sessionRateDao, times(1)).bulkInsertSessionRates(anyList());
    }

    @Test
    void updateSession_avetVisibilityRatesTest() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setName("test-mod");
        updateSessionDTO.setId(1L);
        updateSessionDTO.setRates(Arrays.asList(new RateDTO(1L, true), new RateDTO(2L, false)));
        SessionRecord sessionRecord = initSession(1);
        sessionRecord.setEventType(EventType.AVET.getId());
        Mockito.when(sessionDao.findSession(Mockito.any())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initAvetEvent());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initMultipleRates());
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);

        CpanelSesionTarifaRecord cpanelSesionTarifaRecord1 = sesionTarifaRecord(1, true);
        CpanelSesionTarifaRecord cpanelSesionTarifaRecord2 = sesionTarifaRecord(2, false);
        CpanelSesionTarifaRecord cpanelSesionTarifaRecord3 = sesionTarifaRecord(3, false);

        List<CpanelSesionTarifaRecord> sessionRates = Arrays.asList(cpanelSesionTarifaRecord1, cpanelSesionTarifaRecord2,
                cpanelSesionTarifaRecord3);
        Mockito.when(sessionRateDao.getSessionRatesBySessionId(Mockito.any())).thenReturn(sessionRates);
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(new SessionConfig());

        sessionService.updateSession(1L, updateSessionDTO);

        Mockito.doAnswer(a -> {
            UpdateSessionRequestDTO request = (UpdateSessionRequestDTO) a.getArguments()[1];
            Assertions.assertEquals(updateSessionDTO.getName(), request.getName());
            return Void.class;
        }).when(sessionDao).update(any());
        Mockito.verify(sessionRateDao, times(3)).updateSesionTarifaVisibilities(any());
    }

    @Test
    void updateSession_ratesNoChange() {

        UpdateSessionRequestDTO request = new UpdateSessionRequestDTO();
        request.setId(1L);
        request.setRates(Arrays.asList(new RateDTO(1L, true), new RateDTO(2L, false)));
        SessionRecord sessionRecord = initSession(1);

        Mockito.when(sessionDao.findSession(Mockito.any())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initAvetEvent());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initMultipleRates());
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(new SessionConfig());

        //No change
        Mockito.when(rateDao.getSessionRates(Mockito.any())).thenReturn(List.of(sesionTarifaRecord(1, true), sesionTarifaRecord(1, false)));
        sessionService.updateSession(1L, request);
        Mockito.verify(sessionRateDao, times(0)).cleanRatesForSessionId(eq(1));
        Mockito.verify(sessionRateDao, times(0)).bulkInsertSessionRates(anyList());

        //Change default
        request.setRates(Arrays.asList(new RateDTO(1L, false), new RateDTO(2L, true)));
        sessionService.updateSession(1L, request);
        Mockito.verify(sessionRateDao, times(1)).cleanRatesForSessionId(eq(1));
        Mockito.verify(sessionRateDao, times(1)).bulkInsertSessionRates(anyList());

    }

    @Test
    void updateSession_ratesChangeVisibility() {

        UpdateSessionRequestDTO request = new UpdateSessionRequestDTO();
        request.setId(1L);
        List<RateDTO> requestRates = List.of(new RateDTO(1L, true));
        request.setRates(requestRates);
        SessionRecord sessionRecord = initSession(1);

        Mockito.when(sessionDao.findSession(Mockito.any())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initAvetEvent());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initMultipleRates());
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(new SessionConfig());

        //Change visibility
        List<CpanelSesionTarifaRecord> dbRates = List.of(sesionTarifaRecord(2, false));
        Mockito.when(rateDao.getSessionRates(Mockito.any())).thenReturn(dbRates);
        sessionService.updateSession(1L, request);
        Mockito.verify(sessionRateDao, times(1)).cleanRatesForSessionId(eq(1));
        Mockito.verify(sessionRateDao, times(1)).bulkInsertSessionRates(anyList());

    }

    @Test
    void updateSessions_bulkWithRelativeDates() {

        SessionDateDTO date = new SessionDateDTO();
        date.setAdmissionEnd(ZonedDateTimeWithRelative.of(RelativeDateTime.builder().operator(UnaryOperator.DECREMENT)
                .quantity(Short.valueOf("999")).unit(ChronoUnit.MINUTES).build()));
        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setName("test-mod");
        updateSessionDTO.setDate(date);
        UpdateSessionsRequestDTO updateSessionsDTO = new UpdateSessionsRequestDTO();
        updateSessionsDTO.setIds(Arrays.asList(1L, 2L, 3L));
        updateSessionsDTO.setValue(updateSessionDTO);

        SessionRecord sessionRecord = initSession(1);
        Mockito.when(sessionDao.findSession(Mockito.any())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());

        sessionService.updateSessions(1L, updateSessionsDTO, false);

        Mockito.verify(sessionDao, never()).bulkUpdateSessions(anyList(), any());
    }

    @Test
    void updateSessions_bulkDelete() {

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setStatus(SessionStatus.DELETED);
        UpdateSessionsRequestDTO updateSessionsDTO = new UpdateSessionsRequestDTO();
        updateSessionsDTO.setIds(Arrays.asList(1L, 2L, 3L));
        updateSessionsDTO.setValue(updateSessionDTO);

        Mockito.when(sessionDao.findSessions(Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(initSession(1), initSession(2)));
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());
        int sessionsToUpdate = 2;
        Mockito.when(sessionDao.bulkUpdateSessions(Mockito.any(), Mockito.any())).thenReturn(sessionsToUpdate);
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.anyLong())).thenReturn(new SessionConfig());

        sessionService.updateSessions(1L, updateSessionsDTO, false);

        Mockito.doAnswer(a -> {
            UpdateSessionRequestDTO request = (UpdateSessionRequestDTO) a.getArguments()[1];
            Assertions.assertEquals(updateSessionDTO.getStatus(), request.getStatus());
            return Void.class;
        }).when(sessionDao).bulkUpdateSessions(anyList(), any());

        Mockito.verify(ordersRepository, times(1)).sessionOperations(anyList());
        Mockito.verify(sessionRateDao, times(sessionsToUpdate)).cleanRatesForSessionId(anyInt());
        Mockito.verify(sessionDao, times(0)).bulkUpdateSessions(anyList(), any());
        Mockito.verify(sessionRateDao, times(0)).bulkInsertSessionRates(anyList());
    }

    @Test
    void getPriceTypes_fromVenueTemplate() {
        boolean useRestrictiveAccess = true;

        Long eventId = ObjectRandomizer.randomLong();
        Long sessionId = ObjectRandomizer.randomLong();
        Long venueTemplateId = ObjectRandomizer.randomLong();
        PriceTypeBaseFilter priceTypeBaseFilter = ObjectRandomizer.random(PriceTypeBaseFilter.class);

        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setIdevento(eventId.intValue());
        sessionRecord.setIdsesion(sessionId.intValue());
        sessionRecord.setVenueTemplateId(venueTemplateId.intValue());
        sessionRecord.setUsaaccesosplantilla(useRestrictiveAccess);
        Mockito.when(sessionValidationHelper.getSessionAndValidateWithEvent(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(priceZoneConfigDao.countTotalByVenueConfigId(anyLong())).thenReturn(1L);

        ZonaPreciosConfigRecord zonaPreciosConfigRecord = new ZonaPreciosConfigRecord();
        zonaPreciosConfigRecord.setIdzona(ObjectRandomizer.randomInteger());
        zonaPreciosConfigRecord.setDescripcion(ObjectRandomizer.randomString());
        zonaPreciosConfigRecord.setRestrictiveaccess((byte) 1);
        zonaPreciosConfigRecord.setGateId(ObjectRandomizer.randomLong());
        Mockito.when(priceZoneConfigDao.getPriceZone(anyLong(), any())).thenReturn(Arrays.asList(zonaPreciosConfigRecord));

        PriceTypesDTO priceTypes = sessionService.getPriceTypes(eventId, sessionId, priceTypeBaseFilter);

        Assertions.assertEquals(1, priceTypes.getData().size());
        Assertions.assertEquals(zonaPreciosConfigRecord.getIdzona().longValue(), priceTypes.getData().get(0).getId().longValue());
        Assertions.assertEquals(zonaPreciosConfigRecord.getDescripcion(), priceTypes.getData().get(0).getName());
        Assertions.assertEquals(true, priceTypes.getData().get(0).getAdditionalConfig().getRestrictiveAccess());
        Assertions.assertEquals(zonaPreciosConfigRecord.getGateId(), priceTypes.getData().get(0).getAdditionalConfig().getGateId());
    }

    @Test
    void getPriceTypes_fromSession() {
        boolean useRestrictiveAccess = false;

        Long eventId = ObjectRandomizer.randomLong();
        Long sessionId = ObjectRandomizer.randomLong();
        Long venueTemplateId = ObjectRandomizer.randomLong();
        PriceTypeBaseFilter priceTypeBaseFilter = ObjectRandomizer.random(PriceTypeBaseFilter.class);

        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setIdevento(eventId.intValue());
        sessionRecord.setIdsesion(sessionId.intValue());
        sessionRecord.setVenueTemplateId(venueTemplateId.intValue());
        sessionRecord.setUsaaccesosplantilla(useRestrictiveAccess);
        Mockito.when(sessionValidationHelper.getSessionAndValidateWithEvent(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(priceZoneConfigDao.countTotalByVenueConfigId(anyLong())).thenReturn(1L);

        ZonaPreciosConfigRecord zonaPreciosConfigRecord = new ZonaPreciosConfigRecord();
        zonaPreciosConfigRecord.setIdzona(ObjectRandomizer.randomInteger());
        zonaPreciosConfigRecord.setDescripcion(ObjectRandomizer.randomString());
        zonaPreciosConfigRecord.setRestrictiveaccess((byte) 1);
        zonaPreciosConfigRecord.setGateId(ObjectRandomizer.randomLong());
        Mockito.when(priceZoneConfigDao.getPriceZoneBySession(anyLong(), any(), anyLong()))
                .thenReturn(Arrays.asList(zonaPreciosConfigRecord));

        PriceTypesDTO priceTypes = sessionService.getPriceTypes(eventId, sessionId, priceTypeBaseFilter);

        Assertions.assertEquals(1, priceTypes.getData().size());
        Assertions.assertEquals(zonaPreciosConfigRecord.getIdzona().longValue(), priceTypes.getData().get(0).getId().longValue());
        Assertions.assertEquals(zonaPreciosConfigRecord.getDescripcion(), priceTypes.getData().get(0).getName());
        Assertions.assertEquals(true, priceTypes.getData().get(0).getAdditionalConfig().getRestrictiveAccess());
        Assertions.assertEquals(zonaPreciosConfigRecord.getGateId(), priceTypes.getData().get(0).getAdditionalConfig().getGateId());
    }

    @Test
    void updateGateId() {
        Long eventId = ObjectRandomizer.randomLong();
        Long sessionId = ObjectRandomizer.randomLong();
        Long priceTypeId = ObjectRandomizer.randomLong();
        PriceTypeRequestDTO priceTypeRequestDTO = ObjectRandomizer.random(PriceTypeRequestDTO.class);
        priceTypeRequestDTO.getAdditionalConfig().setGateId(10L);

        Long venueTemplateId = ObjectRandomizer.randomLong();

        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setIdevento(eventId.intValue());
        sessionRecord.setIdsesion(sessionId.intValue());
        sessionRecord.setVenueTemplateId(venueTemplateId.intValue());
        Mockito.when(sessionValidationHelper.getSessionAndValidateWithEvent(eq(eventId), eq(sessionId))).thenReturn(sessionRecord);

        List<Gate> gates = new ArrayList<>();
        Gate gate = new Gate();
        gate.setId(10L);
        gates.add(gate);
        gate = new Gate();
        gate.setId(11L);
        gates.add(gate);
        Mockito.when(venuesRepository.getGates(anyLong())).thenReturn(gates);

        List<ZonaPreciosConfigRecord> priceTypes = new ArrayList<>();
        ZonaPreciosConfigRecord zonaPreciosConfigRecord = new ZonaPreciosConfigRecord();
        zonaPreciosConfigRecord.setIdzona(priceTypeId.intValue());
        priceTypes.add(zonaPreciosConfigRecord);
        Mockito.when(priceZoneConfigDao.getPriceZone(anyLong(), any())).thenReturn(priceTypes);

        sessionService.updateGateId(eventId, sessionId, priceTypeId, priceTypeRequestDTO);

        Mockito.verify(priceTypeLabelSessionDao, times(1)).delete(eq(priceTypeId), eq(sessionId));
        Mockito.verify(priceTypeLabelSessionDao, times(1)).insert(any());
    }

    @Test
    void updateGateId_invalidGateId() {
        Long eventId = ObjectRandomizer.randomLong();
        Long sessionId = ObjectRandomizer.randomLong();
        Long priceTypeId = ObjectRandomizer.randomLong();
        PriceTypeRequestDTO priceTypeRequestDTO = ObjectRandomizer.random(PriceTypeRequestDTO.class);
        priceTypeRequestDTO.getAdditionalConfig().setGateId(111L);

        Long venueTemplateId = ObjectRandomizer.randomLong();

        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setIdevento(eventId.intValue());
        sessionRecord.setIdsesion(sessionId.intValue());
        sessionRecord.setVenueTemplateId(venueTemplateId.intValue());
        Mockito.when(sessionValidationHelper.getSessionAndValidateWithEvent(eq(eventId), eq(sessionId))).thenReturn(sessionRecord);

        List<ZonaPreciosConfigRecord> priceTypes = new ArrayList<>();
        ZonaPreciosConfigRecord zonaPreciosConfigRecord = new ZonaPreciosConfigRecord();
        zonaPreciosConfigRecord.setIdzona(priceTypeId.intValue());
        priceTypes.add(zonaPreciosConfigRecord);
        Mockito.when(priceZoneConfigDao.getPriceZone(anyLong(), any())).thenReturn(priceTypes);

        List<Gate> gates = new ArrayList<>();
        Gate gate = new Gate();
        gate.setId(10L);
        gates.add(gate);
        gate = new Gate();
        gate.setId(11L);
        gates.add(gate);
        Mockito.when(venuesRepository.getGates(anyLong())).thenReturn(gates);

        try {
            sessionService.updateGateId(eventId, sessionId, priceTypeId, priceTypeRequestDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.GATE_ID_INVALID.getErrorCode(), e.getErrorCode());
        }
    }

    private CpanelEntidadRecintoConfigRecord initVenue() {
        CpanelEntidadRecintoConfigRecord venue = new CpanelEntidadRecintoConfigRecord();
        venue.setIdconfiguracion(1);
        venue.setIdrelacionentrecinto(1);
        return venue;
    }

    private List<RateRecord> initRates() {
        List<RateRecord> rates = new ArrayList<>();
        RateRecord r1 = new RateRecord();
        r1.setIdTarifa(1);
        r1.setDefecto(0);
        rates.add(r1);
        return rates;
    }

    private List<RateRecord> initMultipleRates() {
        List<RateRecord> rates = new ArrayList<>();
        RateRecord r1 = initRate(1);
        rates.add(r1);
        RateRecord r2 = initRate(2);
        rates.add(r2);
        RateRecord r3 = initRate(3);
        rates.add(r3);
        return rates;
    }

    private RateRecord initRate() {
        RateRecord r1 = new RateRecord();
        r1.setIdTarifa(1);
        r1.setDefecto(0);
        r1.setNombre("MANTENIMIENTO");
        return r1;
    }

    private RateRecord initRate(Integer id) {
        RateRecord r1 = new RateRecord();
        r1.setIdTarifa(id);
        r1.setDefecto(0);
        r1.setNombre("MANTENIMIENTO: " + id);
        return r1;
    }

    private CreateSessionDTO initSessionDTO() {
        CreateSessionDTO createSessionDTO = new CreateSessionDTO();
        createSessionDTO.setEventId(1L);
        createSessionDTO.setName("test");
        createSessionDTO.setVenueConfigId(1L);
        createSessionDTO.setRates(new ArrayList<>());
        createSessionDTO.getRates().add(new RateDTO(1L, true));
        createSessionDTO.setSessionStartDate(ZonedDateTime.now());
        createSessionDTO.setPublishDate(ZonedDateTime.now().minusHours(1));
        createSessionDTO.setSalesStartDate(ZonedDateTime.now().minusHours(1));
        createSessionDTO.setSalesEndDate(ZonedDateTime.now().plusHours(2));
        createSessionDTO.setTaxId(1L);
        createSessionDTO.setChargeTaxId(1L);
        createSessionDTO.setTicketTaxIds(List.of(1L));
        createSessionDTO.setChargeTaxIds(List.of(1L));
        return createSessionDTO;
    }

    private CpanelEventoRecord initEvent() {
        return initEvent(EventType.NORMAL);
    }

    private CpanelEventoRecord initAvetEvent() {
        return initEvent(EventType.AVET);
    }

    private CpanelEventoRecord initEvent(EventType eventType) {
        final CpanelEventoRecord cpanelEventoRecord = new CpanelEventoRecord();
        cpanelEventoRecord.setEstado(EventType.NORMAL.getId());
        cpanelEventoRecord.setTipoabono((byte) 1);
        cpanelEventoRecord.setIdevento(1);
        cpanelEventoRecord.setTipoevento(eventType.getId());
        cpanelEventoRecord.setIdpromotor(1);
        cpanelEventoRecord.setInvoiceprefixid(1);
        return cpanelEventoRecord;
    }

    private CpanelSesionTarifaRecord sesionTarifaRecord(Integer idTarifa, Boolean defecto) {
        CpanelSesionTarifaRecord cpanelSesionTarifaRecord = new CpanelSesionTarifaRecord();
        cpanelSesionTarifaRecord.setIdsesion(1);
        cpanelSesionTarifaRecord.setIdtarifa(idTarifa);
        cpanelSesionTarifaRecord.setVisibilidad(true);
        cpanelSesionTarifaRecord.setDefecto(defecto);
        return cpanelSesionTarifaRecord;
    }

    private EventAvetConfig initEventAvetConfig(Boolean isSocket) {
        final EventAvetConfig eventAvetConfig = new EventAvetConfig();
        eventAvetConfig.setEventId(1);
        eventAvetConfig.setIsSocket(isSocket);
        return eventAvetConfig;
    }

    private SessionRecord initSession(Integer id) {
        return initSession(id, null);
    }

    private SessionRecord initSession(Integer id, Integer idExterno) {
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setIdsesion(id);
        sessionRecord.setIdexterno(idExterno);
        sessionRecord.setIdevento(1);
        sessionRecord.setEstado(SessionStatus.READY.getId());
        sessionRecord.setFechainiciosesion(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now()));
        sessionRecord.setFechapublicacion(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now().minusHours(1)));
        sessionRecord.setFechaventa(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now().minusHours(1)));
        sessionRecord.setFechafinsesion(CommonUtils.zonedDateTimeToTimestamp(ZonedDateTime.now().plusHours(2)));
        sessionRecord.setEntityId(2);
        return sessionRecord;
    }

    @Test
    void calculateOlsonIdTest() {
        SessionSearchFilter filter = new SessionSearchFilter();
        VenueTemplate venueTemplate = new VenueTemplate();
        TimeZoneDTO timeZoneDTO = new TimeZoneDTO();
        timeZoneDTO.setOlsonId("Europe/Berlin");
        Venue venue = new Venue();
        venue.setTimezone(timeZoneDTO);
        venueTemplate.setVenue(venue);

        filter.setVenueConfigId(1L);

        Mockito.when(venuesRepository.getVenueTemplate(Mockito.any())).thenReturn(venueTemplate);

        String olsonId = sessionService.calculateOlsonId(filter);
        Assertions.assertEquals(timeZoneDTO.getOlsonId(), olsonId);

        filter.setVenueConfigId(null);
        filter.setVenueId(Collections.singletonList(1L));
        Mockito.when(venuesRepository.getVenue(any())).thenReturn(venue);

        olsonId = sessionService.calculateOlsonId(filter);
        Assertions.assertEquals(timeZoneDTO.getOlsonId(), olsonId);

        filter.setVenueId(null);
        when(sessionDao.countDifferentOlsonIds(any())).thenReturn(Collections.singletonList("Europe/Berlin"));

        olsonId = sessionService.calculateOlsonId(filter);
        Assertions.assertEquals(timeZoneDTO.getOlsonId(), olsonId);
    }

    @Test
    void calculateOlsonIdTest_exceptionVenues() {

        SessionSearchFilter filter = new SessionSearchFilter();
        TimeZoneDTO timeZoneDTO = new TimeZoneDTO();
        timeZoneDTO.setOlsonId("Europe/Berlin");
        Venue venue = new Venue();
        venue.setTimezone(timeZoneDTO);
        TimeZoneDTO timeZoneDTO2 = new TimeZoneDTO();
        timeZoneDTO2.setOlsonId("America/NewYork");
        Venue venue2 = new Venue();
        venue2.setTimezone(timeZoneDTO2);

        filter.setVenueId(Arrays.asList(1L, 2L));
        Mockito.when(venuesRepository.getVenue(any())).thenReturn(venue).thenReturn(venue2);

        Assertions.assertThrows(OneboxRestException.class, () -> sessionService.calculateOlsonId(filter));
    }

    @Test
    void calculateOlsonIdTest_exceptionSessions() {

        SessionSearchFilter filter = new SessionSearchFilter();
        when(sessionDao.countDifferentOlsonIds(any())).thenReturn(Arrays.asList("Europe/Berlin", "America/NewYork"));

        Assertions.assertThrows(OneboxRestException.class, () -> sessionService.calculateOlsonId(filter));
    }

    @Test
    void fillSessionPassValuesTest() {

        CreateSessionDTO session = new CreateSessionDTO();
        session.setSeasonSessions(Collections.singletonList(1L));

        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setFechainiciosesion(Timestamp.valueOf("2020-01-01 10:00:00"));
        SessionRecord sessionRecord2 = new SessionRecord();
        sessionRecord2.setFechainiciosesion(Timestamp.valueOf("2019-01-01 10:00:00"));
        List<SessionRecord> sessions = Arrays.asList(sessionRecord, sessionRecord2);
        when(sessionDao.findSessions(any(), any())).thenReturn(sessions);

        sessionService.fillSessionPassValues(session);
        Assertions.assertEquals(session.getSessionStartDate(),
                Timestamp.valueOf("2019-01-01 10:00:00").toLocalDateTime().atOffset(ZoneOffset.UTC).toZonedDateTime());
    }

    @Test
    void validateUpdateWithAlternativeTaxDataProducerDoNotExists() {

        UpdateSessionRequestDTO updateSession = new UpdateSessionRequestDTO();
        updateSession.setEnableProducerTaxData(Boolean.TRUE);

        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        CpanelSesionRecord sesionRecord = new CpanelSesionRecord();
        sesionRecord.setEstado(1);
        eventRecord.setIdentidad(12);
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setEntity(new IdNameDTO());
        producerDTO.getEntity().setId(12L);
        SessionConfig sessionConfig = new SessionConfig();

        when(entitiesRepository.getProducer(any())).thenReturn(producerDTO);

        try {
            sessionService.validateUpdate(updateSession, sesionRecord, eventRecord, sessionConfig, null);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.PRODUCER_ID_DO_NOT_EXIST.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void validateUpdateWithAlternativeTaxDataProducerIdNotValid() {

        UpdateSessionRequestDTO updateSession = new UpdateSessionRequestDTO();
        updateSession.setEnableProducerTaxData(Boolean.TRUE);
        updateSession.setProducerId(11);

        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        CpanelSesionRecord sesionRecord = new CpanelSesionRecord();
        sesionRecord.setEstado(1);
        sesionRecord.setIdpromotor(12);
        eventRecord.setIdentidad(13);
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setEntity(new IdNameDTO());
        producerDTO.getEntity().setId(12L);
        SessionConfig sessionConfig = new SessionConfig();

        when(entitiesRepository.getProducer(any())).thenReturn(producerDTO);

        try {
            sessionService.validateUpdate(updateSession, sesionRecord, eventRecord, sessionConfig, null);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventSessionErrorCode.PRODUCER_ID_NOT_VALID.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void searchSessions_getQueueitInfo() {
        Long eventId = 1L;
        SessionSearchFilter filter = new SessionSearchFilter();
        filter.setId(1L);
        filter.setGetQueueitInfo(true);
        filter.setFields(List.of(SessionField.ID.name(), SessionField.NAME.name(), SessionField.STATUS.name()));

        when(sessionDao.findSessions(any(), any())).thenReturn(Arrays.asList(initSession(1), initSession(2)));

        SessionConfig sessionConfigQueueit = new SessionConfig();
        sessionConfigQueueit.setSessionId(1);
        QueueItConfig queueItConfig = new QueueItConfig();
        queueItConfig.setActive(true);
        sessionConfigQueueit.setQueueItConfig(queueItConfig);

        SessionConfig sessionConfigNoQueueit = new SessionConfig();
        sessionConfigNoQueueit.setSessionId(2);
        List<SessionConfig> sessionConfigs = Arrays.asList(sessionConfigQueueit, sessionConfigNoQueueit);

        when(sessionConfigCouchDao.bulkGet(Arrays.asList(1L, 2L))).thenReturn(sessionConfigs);

        filter.setStatus(Collections.singletonList(SessionStatus.SCHEDULED));
        SessionsDTO sessionsDTO = sessionService.searchSessions(eventId, filter);

        Assertions.assertEquals(sessionsDTO.getData().size(), 2);
        Assertions.assertTrue(sessionsDTO.getData().get(0).getEnableQueue());
    }

    @Test
    void bulkCreateWithSecondaryMarket() {

        List<CreateSessionDTO> sessions = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            CreateSessionDTO sessionDTO = initSessionDTO();
            sessionDTO.setSecondaryMarketStartDate(ZonedDateTime.now().minusHours(2));
            sessionDTO.setSecondaryMarketEndDate(ZonedDateTime.now().minusHours(1));
            sessions.add(sessionDTO);
        }

        Mockito.when(eventDao.getById(Mockito.anyInt())).thenReturn(initEvent(EventType.NORMAL));
        Mockito.doReturn(new CpanelConfigRecintoRecord())
                .when(eventDao).getEventVenueTemplate(Mockito.anyLong(), Mockito.any(), Mockito.any());
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(anyLong())).thenReturn(buildSessionConfigWithSecondaryMarketDates());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());
        Mockito.when(entityVenueTemplateDao.getByVenueTemplateId(Mockito.anyLong())).thenReturn(initVenue());
        Mockito.when(sessionDao.bulkInsertSessions(Mockito.anyList()))
                .thenReturn(ObjectRandomizer.randomListOf(Long.class, sessions.size()));
        Mockito.when(taxDao.getEventTaxes(Mockito.any())).thenReturn(Collections.singletonList(1L));
        mockCheckSecondaryMarketEnabled();


        List<Long> sessionIds = sessionService.createSessions(1L, sessions);

        Assertions.assertEquals(sessions.size(), sessionIds.size());
    }

    @Test
    void update_validateSecondaryMarketDates() {

        UpdateSessionRequestDTO updateSessionDTO = buildUpdateSessionRequestDTO();

        SessionRecord sessionRecord = initSession(1);
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(buildSessionConfigWithSecondaryMarketDates());
        mockCheckSecondaryMarketEnabled();

        sessionService.updateSession(1L, updateSessionDTO);

    }

    private void mockCheckSecondaryMarketEnabled() {
        CpanelEventoRecord mockEvent = initEvent(EventType.NORMAL);
        mockEvent.setIdentidad(1);
        when(eventDao.getById(anyInt())).thenReturn(mockEvent);

        CpanelEntidadRecord mockEntity = new CpanelEntidadRecord();
        mockEntity.setAllowsecmkt((byte) 1);
        EntityDao.EntityInfo entityInfo = random(EntityDao.EntityInfo.class);
        when(entityDao.getEntityInfo(anyInt())).thenReturn(entityInfo);
        when(entityDao.getById(anyInt())).thenReturn(mockEntity);
    }

    private UpdateSessionRequestDTO buildUpdateSessionRequestDTO() {
        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setId(1L);
        updateSessionDTO.setDate(new SessionDateDTO());
        updateSessionDTO.getDate().setStart(ZonedDateTime.now().plusMinutes(30));
        updateSessionDTO.getDate().setSecondaryMarketStart(ZonedDateTimeWithRelative.of(ZonedDateTime.now().plusMinutes(30)));
        updateSessionDTO.getDate().setSecondaryMarketEnd(ZonedDateTimeWithRelative.of(ZonedDateTime.now().plusMinutes(60)));
        updateSessionDTO.setEnableSecondaryMarket(true);

        return updateSessionDTO;
    }

    private SessionConfig buildSessionConfigWithSecondaryMarketDates() {
        SessionSecondaryMarketDates dates = new SessionSecondaryMarketDates();
        dates.setEnabled(true);
        dates.setStartDate(ZonedDateTime.now());
        dates.setEndDate(ZonedDateTime.now().plusMinutes(1));

        SessionConfig sessionConfig = new SessionConfig();
        sessionConfig.setSecondaryMarketDates(dates);

        return sessionConfig;
    }

    @Test
    void postUpdateSession_shouldPublishSessionToExternalProviders_whenStatusChangesToReadyAndEventIsReady() {
        long eventId = 1L;
        long sessionId = 10L;
        long entityId = 100L;

        UpdateSessionRequestDTO request = new UpdateSessionRequestDTO();
        request.setId(sessionId);
        request.setStatus(SessionStatus.READY);

        SessionRecord sessionRecord = initSession((int) sessionId);
        sessionRecord.setEntityId((int) entityId);
        Mockito.when(sessionDao.findSession(sessionId)).thenReturn(sessionRecord);

        EventConfig eventConfig = new EventConfig();
        eventConfig.setInventoryProvider(Provider.ITALIAN_COMPLIANCE);
        Mockito.when(eventConfigCouchDao.get(String.valueOf(eventId))).thenReturn(eventConfig);

        // Mock event in READY status
        EventRecord eventRecord = new EventRecord();
        eventRecord.setEstado(EventStatus.READY.getId());
        eventRecord.setIdentidad((int) entityId);
        Map.Entry<EventRecord, List<VenueRecord>> eventEntry = Map.entry(eventRecord, Collections.emptyList());
        Mockito.when(eventDao.findEvent(eventId)).thenReturn(eventEntry);

        sessionService.postUpdateSession(eventId, request, SessionStatus.SCHEDULED);

        Mockito.verify(intDispatcherRepository, times(1)).publishEvent(entityId, eventId);
    }

    @Test
    void postUpdateSession_shouldNotPublishSession_whenEventIsNotReady() {
        long eventId = 1L;
        long sessionId = 10L;
        long entityId = 100L;

        UpdateSessionRequestDTO request = new UpdateSessionRequestDTO();
        request.setId(sessionId);
        request.setStatus(SessionStatus.READY);

        SessionRecord sessionRecord = initSession((int) sessionId);
        sessionRecord.setEntityId((int) entityId);
        Mockito.when(sessionDao.findSession(sessionId)).thenReturn(sessionRecord);

        EventConfig eventConfig = new EventConfig();
        eventConfig.setInventoryProvider(Provider.ITALIAN_COMPLIANCE);
        Mockito.when(eventConfigCouchDao.get(String.valueOf(eventId))).thenReturn(eventConfig);

        // Mock event in IN_PROGRAMMING status (not READY)
        EventRecord eventRecord = new EventRecord();
        eventRecord.setEstado(EventStatus.IN_PROGRAMMING.getId());
        Map.Entry<EventRecord, List<VenueRecord>> eventEntry = Map.entry(eventRecord, Collections.emptyList());
        Mockito.when(eventDao.findEvent(eventId)).thenReturn(eventEntry);

        sessionService.postUpdateSession(eventId, request, SessionStatus.SCHEDULED);

        Mockito.verify(intDispatcherRepository, never()).publishEvent(anyLong(), anyLong());
    }

    @Test
    void postUpdateSession_shouldNotPublishSession_whenStatusNotChangingToReady() {
        long eventId = 1L;
        long sessionId = 10L;

        UpdateSessionRequestDTO request = new UpdateSessionRequestDTO();
        request.setId(sessionId);
        request.setStatus(SessionStatus.SCHEDULED);

        SessionRecord sessionRecord = initSession((int) sessionId);
        Mockito.when(sessionDao.findSession(sessionId)).thenReturn(sessionRecord);

        sessionService.postUpdateSession(eventId, request, SessionStatus.PLANNED);

        Mockito.verify(intDispatcherRepository, never()).publishEvent(anyLong(), anyLong());
    }

    @Test
    void postUpdateSession_shouldNotPublishSession_whenAlreadyReady() {
        long eventId = 1L;
        long sessionId = 10L;

        UpdateSessionRequestDTO request = new UpdateSessionRequestDTO();
        request.setId(sessionId);
        request.setStatus(SessionStatus.READY);

        SessionRecord sessionRecord = initSession((int) sessionId);
        Mockito.when(sessionDao.findSession(sessionId)).thenReturn(sessionRecord);

        sessionService.postUpdateSession(eventId, request, SessionStatus.READY);

        Mockito.verify(intDispatcherRepository, never()).publishEvent(anyLong(), anyLong());
    }

    @Test
    void postUpdateSession_shouldNotPublishSession_whenNotItalianComplianceProvider() {
        long eventId = 1L;
        long sessionId = 10L;
        long entityId = 100L;

        UpdateSessionRequestDTO request = new UpdateSessionRequestDTO();
        request.setId(sessionId);
        request.setStatus(SessionStatus.READY);

        SessionRecord sessionRecord = initSession((int) sessionId);
        sessionRecord.setEntityId((int) entityId);
        Mockito.when(sessionDao.findSession(sessionId)).thenReturn(sessionRecord);

        EventConfig eventConfig = new EventConfig();
        eventConfig.setInventoryProvider(Provider.SGA);
        Mockito.when(eventConfigCouchDao.get(String.valueOf(eventId))).thenReturn(eventConfig);

        sessionService.postUpdateSession(eventId, request, SessionStatus.SCHEDULED);

        Mockito.verify(intDispatcherRepository, never()).publishEvent(anyLong(), anyLong());
    }

    @Test
    void postUpdateSession_shouldNotPublishSession_whenNoInventoryProvider() {
        long eventId = 1L;
        long sessionId = 10L;
        long entityId = 100L;

        UpdateSessionRequestDTO request = new UpdateSessionRequestDTO();
        request.setId(sessionId);
        request.setStatus(SessionStatus.READY);

        SessionRecord sessionRecord = initSession((int) sessionId);
        sessionRecord.setEntityId((int) entityId);
        Mockito.when(sessionDao.findSession(sessionId)).thenReturn(sessionRecord);

        Mockito.when(eventConfigCouchDao.get(String.valueOf(eventId))).thenReturn(null);

        sessionService.postUpdateSession(eventId, request, SessionStatus.SCHEDULED);

        Mockito.verify(intDispatcherRepository, never()).publishEvent(anyLong(), anyLong());
    }

    @Test
    void postUpdateSession_shouldHandlePublishError() {
        long eventId = 1L;
        long sessionId = 10L;
        long entityId = 100L;

        UpdateSessionRequestDTO request = new UpdateSessionRequestDTO();
        request.setId(sessionId);
        request.setStatus(SessionStatus.READY);

        SessionRecord sessionRecord = initSession((int) sessionId);
        sessionRecord.setEntityId((int) entityId);
        Mockito.when(sessionDao.findSession(sessionId)).thenReturn(sessionRecord);

        EventConfig eventConfig = new EventConfig();
        eventConfig.setInventoryProvider(Provider.ITALIAN_COMPLIANCE);
        Mockito.when(eventConfigCouchDao.get(String.valueOf(eventId))).thenReturn(eventConfig);

        // Mock event in READY status
        EventRecord eventRecord = new EventRecord();
        eventRecord.setEstado(EventStatus.READY.getId());
        eventRecord.setIdentidad((int) entityId);
        Map.Entry<EventRecord, List<VenueRecord>> eventEntry = Map.entry(eventRecord, Collections.emptyList());
        Mockito.when(eventDao.findEvent(eventId)).thenReturn(eventEntry);

        Mockito.doThrow(new RuntimeException("External service unavailable"))
                .when(intDispatcherRepository).publishEvent(anyLong(), anyLong());

        // Should not throw exception - error should be caught and logged
        sessionService.postUpdateSession(eventId, request, SessionStatus.SCHEDULED);

        Mockito.verify(intDispatcherRepository, times(1)).publishEvent(entityId, eventId);
    }

    @Test
    void updateSession_shouldBlockRateEditing_whenItalianComplianceAndBothEventAndSessionReady() {
        long eventId = 1L;
        long sessionId = 1L;

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setId(sessionId);
        updateSessionDTO.setRates(Collections.singletonList(new RateDTO(1L, true)));

        SessionRecord sessionRecord = initSession((int) sessionId);
        sessionRecord.setEventStatus(EventStatus.READY.getId());
        sessionRecord.setEstado(SessionStatus.READY.getId());
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());

        EventConfig eventConfig = new EventConfig();
        eventConfig.setInventoryProvider(Provider.ITALIAN_COMPLIANCE);
        Mockito.when(eventConfigCouchDao.get(String.valueOf(eventId))).thenReturn(eventConfig);

        try {
            sessionService.updateSession(eventId, updateSessionDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventRateErrorCode.RATE_EDITING_BLOCKED.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void updateSession_shouldAllowRateEditing_whenOnlyEventIsReady() {
        long eventId = 1L;
        long sessionId = 1L;

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setId(sessionId);
        updateSessionDTO.setRates(Collections.singletonList(new RateDTO(1L, true)));

        SessionRecord sessionRecord = initSession((int) sessionId);
        sessionRecord.setEventStatus(EventStatus.READY.getId());
        sessionRecord.setEstado(SessionStatus.SCHEDULED.getId());
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(new SessionConfig());

        EventConfig eventConfig = new EventConfig();
        eventConfig.setInventoryProvider(Provider.ITALIAN_COMPLIANCE);
        Mockito.when(eventConfigCouchDao.get(String.valueOf(eventId))).thenReturn(eventConfig);

        Mockito.when(rateDao.getSessionRates(Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());

        // Should not throw exception
        sessionService.updateSession(eventId, updateSessionDTO);
    }

    @Test
    void updateSession_shouldAllowRateEditing_whenOnlySessionIsReady() {
        long eventId = 1L;
        long sessionId = 1L;

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setId(sessionId);
        updateSessionDTO.setRates(Collections.singletonList(new RateDTO(1L, true)));

        SessionRecord sessionRecord = initSession((int) sessionId);
        sessionRecord.setEventStatus(EventStatus.IN_PROGRAMMING.getId());
        sessionRecord.setEstado(SessionStatus.READY.getId());
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(new SessionConfig());

        EventConfig eventConfig = new EventConfig();
        eventConfig.setInventoryProvider(Provider.ITALIAN_COMPLIANCE);
        Mockito.when(eventConfigCouchDao.get(String.valueOf(eventId))).thenReturn(eventConfig);

        Mockito.when(rateDao.getSessionRates(Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());

        // Should not throw exception
        sessionService.updateSession(eventId, updateSessionDTO);
    }

    @Test
    void updateSession_shouldAllowRateEditing_whenNotItalianComplianceProvider() {
        long eventId = 1L;
        long sessionId = 1L;

        UpdateSessionRequestDTO updateSessionDTO = new UpdateSessionRequestDTO();
        updateSessionDTO.setId(sessionId);
        updateSessionDTO.setRates(Collections.singletonList(new RateDTO(1L, true)));

        SessionRecord sessionRecord = initSession((int) sessionId);
        sessionRecord.setEventStatus(EventStatus.READY.getId());
        sessionRecord.setEstado(SessionStatus.READY.getId());
        Mockito.when(sessionValidationHelper.getSessionAndValidate(anyLong(), anyLong())).thenReturn(sessionRecord);
        Mockito.when(eventDao.getById(Mockito.any())).thenReturn(initEvent());
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.any())).thenReturn(new SessionConfig());

        EventConfig eventConfig = new EventConfig();
        eventConfig.setInventoryProvider(Provider.SGA);
        Mockito.when(eventConfigCouchDao.get(String.valueOf(eventId))).thenReturn(eventConfig);

        Mockito.when(rateDao.getSessionRates(Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(rateDao.getRatesByEventId(Mockito.any())).thenReturn(initRates());

        // Should not throw exception
        sessionService.updateSession(eventId, updateSessionDTO);
    }

}
