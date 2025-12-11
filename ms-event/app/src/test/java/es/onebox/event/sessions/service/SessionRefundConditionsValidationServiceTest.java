package es.onebox.event.sessions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.ms.ticket.repository.SessionRepository;
import es.onebox.event.datasources.ms.venue.dto.QuotaDTO;
import es.onebox.event.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dao.record.ZonaPreciosConfigRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionRefundConditions;
import es.onebox.event.sessions.domain.sessionconfig.SessionRefundedSeatQuota;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.PriceTypeAndRateCondition;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.SessionConditionsMap;
import es.onebox.event.venues.dao.BlockingReasonDao;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static es.onebox.event.exception.MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_BLOCKING_REASON;
import static es.onebox.event.exception.MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_PERCENTAGE;
import static es.onebox.event.exception.MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_PRICETYPES;
import static es.onebox.event.exception.MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_QUOTA;
import static es.onebox.event.exception.MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_RATES;
import static es.onebox.event.exception.MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_SEAT_STATUS;
import static es.onebox.event.exception.MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_SESSIONS;
import static es.onebox.event.exception.MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_TOTAL_PERCENTAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class SessionRefundConditionsValidationServiceTest {

    private final String KEY_SEPARATOR = "_";

    private final Integer VENUE_TEMPLATE_ID = 0;
    private final Integer SEASON_SESSION_ID = 2;
    private final Long QUOTA1_ID = 3L;
    private final Long QUOTA2_ID = 4L;
    private final Long BLOCKING_REASON_ID = 5L;
    private final TicketStatus SEAT_STATUS = TicketStatus.BLOCKED_PROMOTER;
    private final Long SESSION1_ID = 6L;
    private final Long SESSION2_ID = 7L;
    private final Long SESSION3_ID = 8L;
    private final Long SESSION4_ID = 9L;
    private final Long PRICE_TYPE1_ID = 10L;
    private final Long PRICE_TYPE2_ID = 11L;
    private final Long RATE1_ID = 12L;
    private final Long RATE2_ID = 13L;
    private final List<Long> VENUE_TEMPLATE_BLOCKING_REASONS = Arrays.asList(14L, 15L);


    @Mock
    private PriceTypeConfigDao priceZoneConfigDao;
    @Mock
    private RateDao rateDao;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private VenuesRepository venuesRepository;
    @Mock
    private BlockingReasonDao blockingReasonDao;
    @InjectMocks
    private SessionRefundConditionsValidationService validationService;

    private SessionRecord session;
    private SessionRefundConditions currentEntity;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        session = prepareSessionRecord();
        currentEntity = buildSessionRefundConditions();
    }

    @Test
    public void validate_failWithBlockedPromotedSeatStatusWithoutBlockingReason() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setRefundedSeatStatus(SEAT_STATUS);

        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> validationService.validate(currentEntity, newEntity, session));
        assertEquals(ex.getErrorCode(), SESSION_REFUND_CONDITIONS_INVALID_BLOCKING_REASON.getErrorCode());
    }

    @Test
    public void validate_failWithBlockingReasonWithoutSeatStatus() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setRefundedSeatBlockReasonId(BLOCKING_REASON_ID);

        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> validationService.validate(currentEntity, newEntity, session));
        assertEquals(ex.getErrorCode(), SESSION_REFUND_CONDITIONS_INVALID_SEAT_STATUS.getErrorCode());
    }

    @Test
    public void validate_failWithSeatStatusAndInvalidBlockingReason() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setRefundedSeatStatus(SEAT_STATUS);
        newEntity.setRefundedSeatBlockReasonId(BLOCKING_REASON_ID);

        prepareBlockingReasons(false);

        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> validationService.validate(currentEntity, newEntity, session));
        assertEquals(ex.getErrorCode(), SESSION_REFUND_CONDITIONS_INVALID_BLOCKING_REASON.getErrorCode());
    }

    @Test
    public void validate_failWithEnabledAndEmptyQuota() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setRefundedSeatQuota(buildRefundedSeatQuota(null));

        prepareQuotas();

        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> validationService.validate(currentEntity, newEntity, session));
        assertEquals(ex.getErrorCode(), SESSION_REFUND_CONDITIONS_INVALID_QUOTA.getErrorCode());
    }

    @Test
    public void validate_failWithEnabledAndInvalidQuota() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setRefundedSeatQuota(buildRefundedSeatQuota(9999L));

        prepareQuotas();

        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> validationService.validate(currentEntity, newEntity, session));
        assertEquals(ex.getErrorCode(), SESSION_REFUND_CONDITIONS_INVALID_QUOTA.getErrorCode());
    }

    @Test
    public void validate_failWithMatrixInvalidSessions() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setSeasonPassRefundConditions(buildSeasonPassRefundConditions(
                Arrays.asList(SESSION1_ID, 666L),
                Arrays.asList(PRICE_TYPE1_ID, PRICE_TYPE2_ID),
                Arrays.asList(RATE1_ID, RATE2_ID), 50D));

        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> validationService.validate(currentEntity, newEntity, session));
        assertEquals(ex.getErrorCode(), SESSION_REFUND_CONDITIONS_INVALID_SESSIONS.getErrorCode());
    }

    @Test
    public void validate_failWithMatrixInvalidPriceTypes() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setSeasonPassRefundConditions(buildSeasonPassRefundConditions(
                Arrays.asList(SESSION1_ID, SESSION2_ID),
                Arrays.asList(PRICE_TYPE1_ID, 666L),
                Arrays.asList(RATE1_ID, RATE2_ID), 50D));

        preparePriceTypes();

        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> validationService.validate(currentEntity, newEntity, session));
        assertEquals(ex.getErrorCode(), SESSION_REFUND_CONDITIONS_INVALID_PRICETYPES.getErrorCode());
    }

    @Test
    public void validate_failWithMatrixInvalidRates() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setSeasonPassRefundConditions(buildSeasonPassRefundConditions(
                Arrays.asList(SESSION1_ID, SESSION2_ID),
                Arrays.asList(PRICE_TYPE1_ID, PRICE_TYPE2_ID),
                Arrays.asList(RATE1_ID, 666L), 50D));

        preparePriceTypes();
        prepareRates();

        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> validationService.validate(currentEntity, newEntity, session));
        assertEquals(ex.getErrorCode(), SESSION_REFUND_CONDITIONS_INVALID_RATES.getErrorCode());
    }

    @Test
    public void validate_failWithMatrixInvalidPercentageValues() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setSeasonPassRefundConditions(buildSeasonPassRefundConditions(
                Arrays.asList(SESSION1_ID, SESSION2_ID),
                Arrays.asList(PRICE_TYPE1_ID, PRICE_TYPE2_ID),
                Arrays.asList(RATE1_ID, RATE2_ID), 102D));

        preparePriceTypes();
        prepareRates();

        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> validationService.validate(currentEntity, newEntity, session));
        assertEquals(ex.getErrorCode(), SESSION_REFUND_CONDITIONS_INVALID_PERCENTAGE.getErrorCode());


        newEntity.setSeasonPassRefundConditions(buildSeasonPassRefundConditions(
                Arrays.asList(SESSION1_ID, SESSION2_ID),
                Arrays.asList(PRICE_TYPE1_ID, PRICE_TYPE2_ID),
                Arrays.asList(RATE1_ID, RATE2_ID), -2D));

        ex = assertThrows(OneboxRestException.class,
                () -> validationService.validate(currentEntity, newEntity, session));
        assertEquals(ex.getErrorCode(), SESSION_REFUND_CONDITIONS_INVALID_PERCENTAGE.getErrorCode());
    }

    @Test
    public void validate_failWithMatrixInvalidTotalPercentageByPriceTypeAndRate() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setSeasonPassRefundConditions(buildSeasonPassRefundConditions(
                Arrays.asList(SESSION1_ID),
                Arrays.asList(PRICE_TYPE1_ID),
                Arrays.asList(RATE1_ID), 33.5));

        preparePriceTypes();
        prepareRates();

        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> validationService.validate(currentEntity, newEntity, session));
        assertEquals(ex.getErrorCode(), SESSION_REFUND_CONDITIONS_INVALID_TOTAL_PERCENTAGE.getErrorCode());
    }

    @Test
    public void validate_OkWithManualMatrixValues() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setSeasonPackAutomaticCalculateConditions(false);
        newEntity.setSeasonPassRefundConditions(buildSeasonPassRefundConditions(
                Arrays.asList(SESSION1_ID, SESSION4_ID),
                Arrays.asList(PRICE_TYPE1_ID),
                Arrays.asList(RATE1_ID), null));
        newEntity.getSeasonPassRefundConditions().get(SESSION1_ID).getRefundPercentages()
                .get(PRICE_TYPE1_ID + KEY_SEPARATOR + RATE1_ID).setRefundPercentage(20D);
        newEntity.getSeasonPassRefundConditions().get(SESSION4_ID).getRefundPercentages()
                .get(PRICE_TYPE1_ID + KEY_SEPARATOR + RATE1_ID).setRefundPercentage(30D);

        preparePriceTypes();
        prepareRates();

        validationService.validate(currentEntity, newEntity, session);
    }

    @Test
    public void validate_OkWithAutomaticMatrixValues() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setSeasonPackAutomaticCalculateConditions(true);

        preparePriceTypes();
        prepareRates();

        validationService.validate(currentEntity, newEntity, session);
    }

    @Test
    public void validate_OkWithNullMatrixValues() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setSeasonPackAutomaticCalculateConditions(false);

        preparePriceTypes();
        prepareRates();

        validationService.validate(currentEntity, newEntity, session);
    }

    @Test
    public void validate_OkWithRefundedSeatStatusDistinctBlockedPromoter() {
        SessionRefundConditions newEntity = new SessionRefundConditions();
        newEntity.setRefundedSeatStatus(TicketStatus.AVAILABLE);

        preparePriceTypes();
        prepareRates();

        validationService.validate(currentEntity, newEntity, session);
    }

    private SessionRefundConditions buildSessionRefundConditions() {
        SessionRefundConditions result = new SessionRefundConditions();
        result.setRefundedSeatQuota(buildRefundedSeatQuota(QUOTA1_ID));
        result.setRefundedSeatStatus(SEAT_STATUS);
        result.setRefundedSeatBlockReasonId(BLOCKING_REASON_ID);
        result.setSeasonPackAutomaticCalculateConditions(false);
        result.setPrintRefundPrice(true);

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

    private SessionRecord prepareSessionRecord() {
        SessionRecord session = new SessionRecord();
        session.setIdsesion(SEASON_SESSION_ID);
        session.setVenueTemplateId(VENUE_TEMPLATE_ID);
        session.setUsaaccesosplantilla(false);
        return session;
    }

    private void prepareQuotas() {
        List<QuotaDTO> quotas = new ArrayList<>();
        QuotaDTO quota1 = new QuotaDTO();
        quota1.setId(QUOTA1_ID);
        quota1.setName(QUOTA1_ID.toString());
        QuotaDTO quota2 = new QuotaDTO();
        quota2.setId(QUOTA2_ID);
        quota2.setName(QUOTA2_ID.toString());
        quotas.add(quota1);
        quotas.add(quota2);
        when(venuesRepository.getQuotas(VENUE_TEMPLATE_ID.longValue()))
                .thenReturn(quotas);
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

    private void prepareBlockingReasons(boolean includeVenueTemplateBlockingReason) {
        List<Long> blockingReasons = VENUE_TEMPLATE_BLOCKING_REASONS;
        if (includeVenueTemplateBlockingReason) {
            blockingReasons.add(BLOCKING_REASON_ID);
        }

        when(blockingReasonDao.findByVenueTemplate(VENUE_TEMPLATE_ID)).thenReturn(blockingReasons);
    }

    private SessionRefundedSeatQuota buildRefundedSeatQuota(Long id) {
        SessionRefundedSeatQuota result = new SessionRefundedSeatQuota();
        result.setId(id);
        result.setEnabled(true);
        return result;
    }
}
