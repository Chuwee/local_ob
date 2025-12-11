package es.onebox.event.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.datasources.integration.avet.config.repository.IntAvetConfigRepository;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.dao.PresaleChannelDao;
import es.onebox.event.sessions.dao.PresaleCustomTypeDao;
import es.onebox.event.sessions.dao.PresaleDao;
import es.onebox.event.sessions.dao.PresaleLoyaltyProgramDao;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.domain.sessionconfig.PreSaleConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.dto.CreateSessionPreSaleConfigDTO;
import es.onebox.event.sessions.dto.SessionPreSaleConfigDTO;
import es.onebox.event.sessions.dto.SessionPresaleUpdateDTO;
import es.onebox.event.sessions.dto.UpdateSessionPreSaleConfigDTO;
import es.onebox.event.sessions.enums.PresaleValidationRangeType;
import es.onebox.event.sessions.enums.PresaleValidatorType;
import es.onebox.event.sessions.service.SessionPresaleService;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPreventaRecord;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.List;

import static es.onebox.utils.ObjectRandomizer.randomString;

class SessionPresaleServiceTest {

    private static final Long SESSION_ID = 1L;
    private static final Long PRESALE_ID = 1L;
    private static final Long VALIDATOR_ID = 1L;
    private static final Long EVENT_ID = 1L;

    @Mock
    private SessionValidationHelper sessionValidationHelper;
    @Mock
    private IntAvetConfigRepository intAvetConfigRepository;
    @Mock
    private RefreshDataService refreshDataService;
    @Mock
    private SessionDao sessionDao;
    @Mock
    private EventDao eventDao;
    @Mock
    private SessionConfigCouchDao sessionConfigCouchDao;
    @Mock
    private PresaleDao presaleDao;
    @Mock
    private PresaleChannelDao presaleChannelDao;
    @Mock
    private PresaleCustomTypeDao presaleCustomTypeDao;
    @Mock
    private PresaleLoyaltyProgramDao presaleLoyaltyProgramDao;

    @InjectMocks
    private SessionPresaleService service;

    @Captor
    private ArgumentCaptor<List<SessionConfig>> sessionConfigCaptor;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPresalesTest() {
        List<Integer> channelIds = ObjectRandomizer.randomListOf(Integer.class, 2);
        CpanelPreventaRecord cpanelPreventaRecord = new CpanelPreventaRecord();
        cpanelPreventaRecord.setIdpreventa(PRESALE_ID.intValue());
        cpanelPreventaRecord.setIdvalidador(VALIDATOR_ID.intValue());
        cpanelPreventaRecord.setTipovalidador(PresaleValidatorType.COLLECTIVE.getId());
        cpanelPreventaRecord.setPermitirrecomprar((byte) 1);
        List<CpanelPreventaRecord> presales = List.of(cpanelPreventaRecord);

        Mockito.when(presaleDao.findSessionPresalesBySessionId(SESSION_ID)).thenReturn(presales);
        Mockito.when(presaleChannelDao.findPresaleChannelIds(PRESALE_ID)).thenReturn(channelIds);

        List<SessionPreSaleConfigDTO> responsePresales = service.getSessionPresales(SESSION_ID);

        Mockito.verify(presaleDao).findSessionPresalesBySessionId(SESSION_ID);
        Mockito.verify(presaleChannelDao).findPresaleChannelIds(PRESALE_ID);

        Assertions.assertEquals(presales.size(), responsePresales.size());
    }

    @Test
    void createSessionPresaleTest() {
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        eventRecord.setTipoevento(1);
        CreateSessionPreSaleConfigDTO createSessionPreSaleConfigDTO = new CreateSessionPreSaleConfigDTO();
        createSessionPreSaleConfigDTO.setName(randomString());
        createSessionPreSaleConfigDTO.setValidatorId(VALIDATOR_ID);
        createSessionPreSaleConfigDTO.setValidatorType(PresaleValidatorType.COLLECTIVE);
        CpanelPreventaRecord cpanelPreventaRecord = new CpanelPreventaRecord();
        cpanelPreventaRecord.setIdpreventa(PRESALE_ID.intValue());
        cpanelPreventaRecord.setIdvalidador(VALIDATOR_ID.intValue());
        cpanelPreventaRecord.setTipovalidador(PresaleValidatorType.COLLECTIVE.getId());
        cpanelPreventaRecord.setPermitirrecomprar((byte) 0);
        SessionConfig sessionConfig = new SessionConfig();

        Mockito.when(eventDao.getById(EVENT_ID.intValue())).thenReturn(eventRecord);
        Mockito.when(presaleDao.insert(Mockito.any())).thenReturn(cpanelPreventaRecord);
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(SESSION_ID)).thenReturn(sessionConfig);
        Mockito.doNothing().when(sessionConfigCouchDao).upsert(SESSION_ID.toString(), sessionConfig);

        SessionPreSaleConfigDTO responsePresale = service.createSessionPresale(EVENT_ID, SESSION_ID, createSessionPreSaleConfigDTO);

        Mockito.verify(presaleDao).insert(Mockito.any());
        Mockito.verify(sessionConfigCouchDao).getOrInitSessionConfig(SESSION_ID);
        Mockito.verify(sessionConfigCouchDao).upsert(SESSION_ID.toString(), sessionConfig);

        Assertions.assertNotNull(responsePresale);
        Assertions.assertEquals(Boolean.FALSE, responsePresale.getActive());
        Assertions.assertEquals(createSessionPreSaleConfigDTO.getValidatorId(), responsePresale.getValidatorId());
        Assertions.assertEquals(createSessionPreSaleConfigDTO.getValidatorType(), responsePresale.getValidatorType());
    }

    @Test
    void createSessionPresaleKOTest() {
        CreateSessionPreSaleConfigDTO createSessionPreSaleConfigDTO = new CreateSessionPreSaleConfigDTO();
        createSessionPreSaleConfigDTO.setName(randomString());
        createSessionPreSaleConfigDTO.setValidatorId(null);
        createSessionPreSaleConfigDTO.setValidatorType(PresaleValidatorType.COLLECTIVE);

        OneboxRestException exception = Assertions.assertThrows(OneboxRestException.class, () ->
                service.createSessionPresale(EVENT_ID, SESSION_ID, createSessionPreSaleConfigDTO));
        Assertions.assertTrue(exception.getErrorCode().contains("INVALID_SESSION_PRESALE_CREATE_REQUEST"));

        createSessionPreSaleConfigDTO.setValidatorId(VALIDATOR_ID);
        createSessionPreSaleConfigDTO.setValidatorType(PresaleValidatorType.CUSTOMERS);

        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                service.createSessionPresale(EVENT_ID, SESSION_ID, createSessionPreSaleConfigDTO));
        Assertions.assertTrue(exception.getErrorCode().contains("INVALID_SESSION_PRESALE_CREATE_REQUEST"));
    }

    @Test
    void updateSessionPresaleTest() {
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();
        eventRecord.setTipoevento(1);
        CpanelPreventaRecord cpanelPreventaRecord = new CpanelPreventaRecord();
        cpanelPreventaRecord.setIdpreventa(PRESALE_ID.intValue());
        cpanelPreventaRecord.setIdvalidador(VALIDATOR_ID.intValue());
        cpanelPreventaRecord.setTipovalidador(PresaleValidatorType.COLLECTIVE.getId());
        cpanelPreventaRecord.setPermitirrecomprar((byte) 0);
        UpdateSessionPreSaleConfigDTO updateSessionPreSaleConfigDTO = new UpdateSessionPreSaleConfigDTO();
        updateSessionPreSaleConfigDTO.setActive(true);
        updateSessionPreSaleConfigDTO.setValidationRangeType(PresaleValidationRangeType.ALL);
        SessionConfig sessionConfig = new SessionConfig();

        Mockito.when(eventDao.getById(EVENT_ID.intValue())).thenReturn(eventRecord);
        Mockito.when(presaleDao.findById(PRESALE_ID.intValue())).thenReturn(cpanelPreventaRecord);
        Mockito.when(presaleDao.update(Mockito.any())).thenReturn(cpanelPreventaRecord);
        Mockito.doNothing().when(presaleChannelDao).deleteByPresaleId(PRESALE_ID.intValue());
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(SESSION_ID)).thenReturn(sessionConfig);
        Mockito.doNothing().when(sessionConfigCouchDao).upsert(SESSION_ID.toString(), sessionConfig);

        service.updateSessionPresale(EVENT_ID, SESSION_ID, PRESALE_ID, updateSessionPreSaleConfigDTO);

        Mockito.verify(presaleDao).findById(PRESALE_ID.intValue());
        Mockito.verify(presaleDao).update(Mockito.any());
        Mockito.verify(sessionConfigCouchDao).getOrInitSessionConfig(SESSION_ID);
        Mockito.verify(sessionConfigCouchDao).upsert(SESSION_ID.toString(), sessionConfig);
    }

    @Test
    void updateSessionPresaleKOTest() {
        CpanelPreventaRecord cpanelPreventaRecord = new CpanelPreventaRecord();
        cpanelPreventaRecord.setIdpreventa(PRESALE_ID.intValue());
        cpanelPreventaRecord.setTipovalidador(PresaleValidatorType.COLLECTIVE.getId());
        UpdateSessionPreSaleConfigDTO updateSessionPreSaleConfigDTO = new UpdateSessionPreSaleConfigDTO();
        updateSessionPreSaleConfigDTO.setValidationRangeType(PresaleValidationRangeType.DATE_RANGE);

        Mockito.when(presaleDao.findById(PRESALE_ID.intValue())).thenReturn(cpanelPreventaRecord);
        OneboxRestException exception = Assertions.assertThrows(OneboxRestException.class, () ->
                service.updateSessionPresale(EVENT_ID, SESSION_ID, PRESALE_ID, updateSessionPreSaleConfigDTO));
        Assertions.assertTrue(exception.getErrorCode().contains("INVALID_SESSION_PRESALE_DATES_REQUIRED"));

        updateSessionPreSaleConfigDTO.setStartDate(ZonedDateTime.now().plusDays(1L));
        updateSessionPreSaleConfigDTO.setEndDate(ZonedDateTime.now());

        Mockito.when(presaleDao.findById(PRESALE_ID.intValue())).thenReturn(cpanelPreventaRecord);
        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                service.updateSessionPresale(EVENT_ID, SESSION_ID, PRESALE_ID, updateSessionPreSaleConfigDTO));
        Assertions.assertTrue(exception.getErrorCode().contains("INVALID_SESSION_PRESALE_DATES_END_BEFORE_START"));

        updateSessionPreSaleConfigDTO.setStartDate(ZonedDateTime.now().minusDays(1L));
        updateSessionPreSaleConfigDTO.setActiveCustomerTypes(List.of(1));

        Mockito.when(presaleDao.findById(PRESALE_ID.intValue())).thenReturn(cpanelPreventaRecord);
        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                service.updateSessionPresale(EVENT_ID, SESSION_ID, PRESALE_ID, updateSessionPreSaleConfigDTO));
        Assertions.assertTrue(exception.getErrorCode().contains("INVALID_SESSION_PRESALE_UPDATE_REQUEST"));

        updateSessionPreSaleConfigDTO.setActiveCustomerTypes(null);
        updateSessionPreSaleConfigDTO.setMemberTicketsLimitEnabled(true);
        updateSessionPreSaleConfigDTO.setMemberTicketsLimit(1);

        Mockito.when(presaleDao.findById(PRESALE_ID.intValue())).thenReturn(cpanelPreventaRecord);
        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                service.updateSessionPresale(EVENT_ID, SESSION_ID, PRESALE_ID, updateSessionPreSaleConfigDTO));
        Assertions.assertTrue(exception.getErrorCode().contains("INVALID_SESSION_PRESALE_UPDATE_REQUEST"));
    }

    @Test
    void deleteSessionPresaleTest() {
        SessionConfig sessionConfig = new SessionConfig();
        PreSaleConfig preSaleConfig = new PreSaleConfig();
        preSaleConfig.setId(PRESALE_ID);
        sessionConfig.setPreSaleConfig(preSaleConfig);
        CpanelPreventaRecord cpanelPreventaRecord = new CpanelPreventaRecord();

        Mockito.when(presaleDao.findById(PRESALE_ID.intValue())).thenReturn(cpanelPreventaRecord);
        Mockito.doNothing().when(presaleDao).deleteByPresaleId(PRESALE_ID.intValue());
        Mockito.doNothing().when(presaleChannelDao).deleteByPresaleId(PRESALE_ID.intValue());
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(SESSION_ID)).thenReturn(sessionConfig);
        Mockito.doNothing().when(sessionConfigCouchDao).upsert(SESSION_ID.toString(), sessionConfig);

        service.deleteSessionPresale(SESSION_ID, PRESALE_ID);

        Mockito.verify(presaleDao).deleteByPresaleId(PRESALE_ID.intValue());
        Mockito.verify(presaleChannelDao).deleteByPresaleId(PRESALE_ID.intValue());
        Mockito.verify(sessionConfigCouchDao).getOrInitSessionConfig(SESSION_ID);
        Mockito.verify(sessionConfigCouchDao).upsert(SESSION_ID.toString(), sessionConfig);
    }

    @Test
    void updatePresalePromotionTest() {
        Long eventId = ObjectRandomizer.randomLong();
        Long promotionId = ObjectRandomizer.randomLong();
        List<SessionPresaleUpdateDTO> request = ObjectRandomizer.randomListOf(SessionPresaleUpdateDTO.class, 5);
        Long amountOfUpdatesInDb = request.stream().map(SessionPresaleUpdateDTO::getStatus).distinct().count();

        SessionConfig sessionConfig = ObjectRandomizer.random(SessionConfig.class);
        Mockito.when(sessionConfigCouchDao.getOrInitSessionConfig(Mockito.anyLong())).thenReturn(sessionConfig);

        service.updatePresale(eventId, promotionId, request);

        Mockito.verify(sessionValidationHelper)
                .getSessionsAndValidateWithEvent(eventId, request.stream().map(SessionPresaleUpdateDTO::getId).toList());
        Mockito.verify(sessionDao, Mockito.times(amountOfUpdatesInDb.intValue()))
                .bulkUpdateSessions(Mockito.any(), Mockito.any());
        Mockito.verify(sessionConfigCouchDao).bulkUpsert(sessionConfigCaptor.capture());

        Assertions.assertEquals(request.size(), sessionConfigCaptor.getValue().size(), "Some session configs not updated");
    }

}
