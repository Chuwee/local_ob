package es.onebox.event.sessions.service;

import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.ms.venue.dto.BlockingReason;
import es.onebox.event.datasources.ms.venue.repository.BlockingReasonsRepository;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dao.record.ZonaPreciosConfigRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionRefundConditions;
import es.onebox.event.sessions.domain.sessionconfig.SessionRefundedSeatQuota;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.PriceTypeAndRateCondition;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.SessionConditionsMap;
import es.onebox.event.sessions.dto.CreateSessionDTO;
import es.onebox.event.sessions.dto.PriceTypeAndRateConditionDTO;
import es.onebox.event.sessions.dto.SessionConditionsDTO;
import es.onebox.event.sessions.dto.SessionRefundConditionsDTO;
import es.onebox.event.sessions.dto.SessionRefundedSeatQuotaDTO;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static es.onebox.utils.ObjectRandomizer.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SessionRefundConditionsServiceTest {

    private final String KEY_SEPARATOR = "_";

    private final Integer EVENT_ID = 1;
    private final Integer VENUE_TEMPLATE_ID = 0;
    private final Integer SEASON_SESSION_ID = 2;
    private final Long QUOTA1_ID = 3L;
    private final Long QUOTA2_ID = 4L;
    private final Long BLOCKING_REASON_ID = 5L;
    private final Long DEFAULT_BLOCKING_REASON_ID = 55L;
    private final TicketStatus SEAT_STATUS = TicketStatus.BLOCKED_PROMOTER;
    private final Long SESSION1_ID = 6L;
    private final Long SESSION2_ID = 7L;
    private final Long SESSION3_ID = 8L;
    private final Long SESSION4_ID = 9L;
    private final Long PRICE_TYPE1_ID = 10L;
    private final Long PRICE_TYPE2_ID = 11L;
    private final Long RATE1_ID = 12L;
    private final Long RATE2_ID = 13L;

    private SessionRefundConditionsService sessionRefundConditionsServiceSpy;
    @Mock
    private SessionValidationHelper sessionValidationHelper;
    @Mock
    private SessionConfigCouchDao sessionConfigCouchDao;
    @Mock
    private SessionRefundConditionsValidationService validationService;
    @Mock
    private SeasonSessionDao seasonSessionDao;
    @Mock
    private PriceTypeConfigDao priceZoneConfigDao;
    @Mock
    private RateDao rateDao;
    @Mock
    private BlockingReasonsRepository blockingReasonsRepository;
    @Captor
    ArgumentCaptor<SessionConfig> sessionConfigArgumentCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sessionRefundConditionsServiceSpy = spy(new SessionRefundConditionsService(seasonSessionDao, sessionValidationHelper,
                sessionConfigCouchDao, priceZoneConfigDao, rateDao, validationService, blockingReasonsRepository));

        prepareEventSessionValidation();
    }

    @Test
    public void getRefundConditions_Ok() {

        prepareSessionConfig(null);
        SessionRefundConditionsDTO result = sessionRefundConditionsServiceSpy.getRefundConditions(EVENT_ID.longValue(),
                SEASON_SESSION_ID.longValue());
        assertNotNull(result);
        assertNotNull(result.getSessionPackRefundConditions());
        assertFalse(result.getSessionPackRefundConditions().isEmpty());
    }

    @Test
    public void updateRefundConditions_OkWithAutomaticMatrixCalculation() throws Exception {
        prepareSessionConfig(buildSessionRefundConditions());
        prepareValidation();
        prepareSeasonSessions();
        preparePriceTypes();
        prepareRates();

        SessionRefundConditionsDTO input = buildSessionRefundConditionsDTO(true);

        sessionRefundConditionsServiceSpy.updateRefundConditions(EVENT_ID.longValue(), SEASON_SESSION_ID.longValue(), input);
        verify(sessionConfigCouchDao, times(1))
                .upsert(eq(SEASON_SESSION_ID.toString()), any(SessionConfig.class));
    }

    @Test
    public void updateRefundConditions_OkWithManualMatrixCalculation() throws Exception {
        prepareSessionConfig(buildSessionRefundConditions());
        prepareValidation();
        prepareSeasonSessions();
        preparePriceTypes();
        prepareRates();

        SessionRefundConditionsDTO input = buildSessionRefundConditionsDTO(false);

        sessionRefundConditionsServiceSpy.updateRefundConditions(EVENT_ID.longValue(), SEASON_SESSION_ID.longValue(), input);
        verify(sessionConfigCouchDao, times(1))
                .upsert(eq(SEASON_SESSION_ID.toString()), any(SessionConfig.class));
    }

    @Test
    public void initRefundConditions_Ok() throws Exception {
        prepareSessionConfig(null);
        prepareBlockingReasons();

        CreateSessionDTO sessionData = buildCreateSessionDTO();

        sessionRefundConditionsServiceSpy.initRefundConditions(SEASON_SESSION_ID.longValue(), sessionData);

        verify(sessionConfigCouchDao, times(1)).upsert(eq(SEASON_SESSION_ID.toString()), any(SessionConfig.class));
        verify(sessionConfigCouchDao).upsert(eq(SEASON_SESSION_ID.toString()), sessionConfigArgumentCaptor.capture());
        Long blockingReasonId = sessionConfigArgumentCaptor.getValue().getSessionRefundConditions().getRefundedSessionPackSeatBlockReasonId();
        assertEquals(DEFAULT_BLOCKING_REASON_ID, blockingReasonId);
    }

    private CreateSessionDTO buildCreateSessionDTO() {
        CreateSessionDTO sessionData = new CreateSessionDTO();
        sessionData.setVenueConfigId(VENUE_TEMPLATE_ID.longValue());
        sessionData.setSeasonSessions(Arrays.asList(SESSION1_ID, SESSION2_ID));
        return sessionData;
    }

    private void prepareBlockingReasons() {
        BlockingReason blockingReason = new BlockingReason();
        blockingReason.setDefault(true);
        blockingReason.setId(DEFAULT_BLOCKING_REASON_ID);
        List<BlockingReason> blockingReasons = Arrays.asList(blockingReason);
        when(blockingReasonsRepository.getBlockingReasons(VENUE_TEMPLATE_ID.longValue())).thenReturn(blockingReasons);
    }

    private void prepareEventSessionValidation() {
        SessionRecord session = new SessionRecord();
        session.setIdsesion(SEASON_SESSION_ID);
        session.setIdevento(EVENT_ID);
        session.setVenueTemplateId(VENUE_TEMPLATE_ID);
        when(sessionValidationHelper.getSessionAndValidateWithEvent(EVENT_ID.longValue(), SEASON_SESSION_ID.longValue()))
                .thenReturn(session);
    }

    private void prepareSessionConfig(SessionRefundConditions currentConditions) {
        SessionConfig sessionConfig = new SessionConfig();
        sessionConfig.setSessionRefundConditions(currentConditions);
        if (currentConditions == null) {
            sessionConfig.setSessionRefundConditions(random(SessionRefundConditions.class));
        }
        when(sessionConfigCouchDao.getOrInitSessionConfig(SEASON_SESSION_ID.longValue())).thenReturn(sessionConfig);
    }

    private void prepareValidation() {
        doNothing().when(validationService).validate(any(SessionRefundConditions.class), any(SessionRefundConditions.class),
                any(SessionRecord.class));
    }

    private SessionRefundConditions buildSessionRefundConditions() {
        SessionRefundConditions result = new SessionRefundConditions();
        result.setRefundedSeatQuota(buildRefundedSeatQuota(QUOTA1_ID));
        result.setRefundedSeatStatus(SEAT_STATUS);
        result.setRefundedSeatBlockReasonId(BLOCKING_REASON_ID);
        result.setSeasonPackAutomaticCalculateConditions(true);
        result.setPrintRefundPrice(true);
        result.setRefundedSessionPackSeatBlockReasonId(BLOCKING_REASON_ID);

        Map<Long, SessionConditionsMap> seasonPassRefundConditions = buildSeasonPassRefundConditions(
                Arrays.asList(SESSION1_ID, SESSION2_ID, SESSION3_ID, SESSION4_ID),
                Arrays.asList(PRICE_TYPE1_ID, PRICE_TYPE2_ID),
                Arrays.asList(RATE1_ID, RATE2_ID), 25D);

        result.setSeasonPassRefundConditions(seasonPassRefundConditions);
        return result;
    }

    private Map<Long, SessionConditionsMap> buildSeasonPassRefundConditions(List<Long> sessionIds, List<Long> priceTypeIds,
                                                                            List<Long> rateIds, Double percentage) {
        return sessionIds.stream().collect(Collectors.toMap(Function.identity(),
                sessionId -> buildSessionConditionsMap(sessionId, priceTypeIds, rateIds, percentage)));
    }

    private SessionConditionsMap buildSessionConditionsMap(Long sessionId, List<Long> priceTypeIds, List<Long> rateIds,
                                                           Double percentage) {

        List<String> priceTypeAndRateIds = priceTypeIds.stream()
                .flatMap(priceTypeId -> rateIds.stream().map(rateId -> priceTypeId + KEY_SEPARATOR + rateId))
                .collect(Collectors.toList());

        Map<String, PriceTypeAndRateCondition> conditionsMap = priceTypeAndRateIds.stream()
                .collect(Collectors.toMap(Function.identity(), id -> buildPriceTypeAndRateCondition(id, percentage)));
        conditionsMap = new TreeMap<>(conditionsMap); //sort by key


        SessionConditionsMap result = new SessionConditionsMap();
        result.setId(sessionId);
        result.setRefundPercentages(conditionsMap);
        return result;
    }

    private PriceTypeAndRateCondition buildPriceTypeAndRateCondition(String priceTypeAndRateId, Double percentage) {
        String priceType = priceTypeAndRateId.split(KEY_SEPARATOR)[0];
        String rate = priceTypeAndRateId.split(KEY_SEPARATOR)[1];

        PriceTypeAndRateCondition result = new PriceTypeAndRateCondition();
        result.setRefundPercentage(percentage);
        result.setPriceTypeId(Integer.valueOf(priceType));
        result.setRateId(Integer.valueOf(rate));
        return result;
    }

    private SessionRefundConditionsDTO buildSessionRefundConditionsDTO(boolean automaticCalculation) {
        Map<Long, SessionConditionsDTO> sessionPackRefundConditions = new HashMap<>();
        if (!automaticCalculation) {
            SessionConditionsDTO session1 = new SessionConditionsDTO();
            session1.setId(SESSION1_ID);
            Map<String, PriceTypeAndRateConditionDTO> refundPercentagesMap1 = new HashMap<>();
            PriceTypeAndRateConditionDTO priceTypeAndRate1 = buildPriceTypeAndRateCondnitionDTO(PRICE_TYPE1_ID, RATE1_ID, 20D);
            PriceTypeAndRateConditionDTO priceTypeAndRate2 = buildPriceTypeAndRateCondnitionDTO(PRICE_TYPE1_ID, RATE2_ID, 30D);
            refundPercentagesMap1.put(PRICE_TYPE1_ID + KEY_SEPARATOR + RATE1_ID, priceTypeAndRate1);
            refundPercentagesMap1.put(PRICE_TYPE1_ID + KEY_SEPARATOR + RATE2_ID, priceTypeAndRate2);
            session1.setRefundPercentages(refundPercentagesMap1);
            sessionPackRefundConditions.put(SESSION1_ID, session1);
        }

        SessionRefundConditionsDTO result = new SessionRefundConditionsDTO();
        result.setRefundedSeatQuota(buildRefundedSeatQuotaDTO(QUOTA1_ID));
        result.setRefundedSeatStatus(SEAT_STATUS);
        result.setRefundedSeatBlockReasonId(BLOCKING_REASON_ID);
        result.setSeasonPackAutomaticCalculateConditions(automaticCalculation);
        result.setPrintRefundPrice(true);
        result.setSessionPackRefundConditions(sessionPackRefundConditions);

        return result;
    }

    private PriceTypeAndRateConditionDTO buildPriceTypeAndRateCondnitionDTO(Long priceTypeId, Long rateId,
                                                                            Double percentage) {
        PriceTypeAndRateConditionDTO result = new PriceTypeAndRateConditionDTO();
        result.setRefundPercentage(percentage);
        result.setPriceTypeId(priceTypeId.intValue());
        result.setRateId(rateId.intValue());
        return result;
    }

    private void prepareSeasonSessions() {
        when(seasonSessionDao.findSessionsBySessionPackId(SEASON_SESSION_ID.longValue())).thenReturn(
                Arrays.asList(SESSION1_ID, SESSION2_ID, SESSION3_ID, SESSION4_ID));
    }

    private void preparePriceTypes() {
        ZonaPreciosConfigRecord zpc1 = new ZonaPreciosConfigRecord();
        zpc1.setIdzona(PRICE_TYPE1_ID.intValue());
        ZonaPreciosConfigRecord zpc2 = new ZonaPreciosConfigRecord();
        zpc2.setIdzona(PRICE_TYPE2_ID.intValue());

        when(priceZoneConfigDao.getPriceZone(VENUE_TEMPLATE_ID.longValue(), null))
                .thenReturn(Arrays.asList(zpc1, zpc2));
        when(priceZoneConfigDao.getPriceZoneBySession(VENUE_TEMPLATE_ID.longValue(), null, SEASON_SESSION_ID.longValue()))
                .thenReturn(Arrays.asList(zpc1, zpc2));
    }

    private void prepareRates() {
        RateRecord rate1 = new RateRecord();
        rate1.setIdTarifa(RATE1_ID.intValue());
        RateRecord rate2 = new RateRecord();
        rate2.setIdTarifa(RATE2_ID.intValue());

        when(rateDao.getRatesBySessionId(eq(SEASON_SESSION_ID), anyLong(), anyLong())).thenReturn(Arrays.asList(rate1, rate2));
    }

    private SessionRefundedSeatQuota buildRefundedSeatQuota(Long id) {
        SessionRefundedSeatQuota result = new SessionRefundedSeatQuota();
        result.setId(id);
        result.setEnabled(true);
        return result;
    }

    private SessionRefundedSeatQuotaDTO buildRefundedSeatQuotaDTO(Long id) {
        SessionRefundedSeatQuotaDTO result = new SessionRefundedSeatQuotaDTO();
        result.setId(id);
        result.setEnabled(true);
        return result;
    }
}
