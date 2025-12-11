package es.onebox.event.sessions.utils;

import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.sessions.dao.record.ZonaPreciosConfigRecord;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.PriceTypeAndRateCondition;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.SessionConditionsMap;
import es.onebox.event.sessions.dto.PriceTypeAndRateConditionDTO;
import es.onebox.event.sessions.dto.SessionConditionsDTO;
import es.onebox.event.sessions.dto.SessionRefundConditionsDTO;
import es.onebox.event.sessions.dto.SessionRefundedSeatQuotaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RefundConditionsUtilsTest {

    private final String KEY_SEPARATOR = "_";

    private final Long SESSION1_ID = 10L;
    private final Long SESSION2_ID = 11L;
    private final Long SESSION3_ID = 12L;
    private final Long SESSION4_ID = 13L;
    private final Long PRICE_TYPE1_ID = 2L;
    private final Long PRICE_TYPE2_ID = 3L;
    private final Long RATE1_ID = 4L;
    private final Long RATE2_ID = 5L;
    private final Long QUOTA1_ID = 6L;
    private final Long BLOCKING_REASON_ID = 7L;
    private final TicketStatus SEAT_STATUS = TicketStatus.BLOCKED_PROMOTER;

    @Test
    public void buildRefundConditionsMap_whenCreatingSeasonPass() {
        List<Long> seasonSessionIds = Arrays.asList(SESSION1_ID, SESSION2_ID, SESSION3_ID, SESSION4_ID);
        Map<Long, SessionConditionsMap> resultMap = RefundConditionsUtils.buildRefundConditionsMap(null,
                seasonSessionIds, buildPriceTypes(), buildRates());

        validate(resultMap);
    }

    @Test
    public void buildRefundConditionsMap_whenUpdatingSeasonPassRefundConditions() {
        List<Long> seasonSessionIds = Arrays.asList(SESSION1_ID, SESSION2_ID, SESSION3_ID, SESSION4_ID);
        Map<Long, SessionConditionsMap> currentMap = buildSeasonPassRefundConditions(
                seasonSessionIds,
                Arrays.asList(PRICE_TYPE1_ID, PRICE_TYPE2_ID),
                Arrays.asList(RATE1_ID, RATE2_ID), 25D);

        Map<Long, SessionConditionsMap> resultMap = RefundConditionsUtils.buildRefundConditionsMap(currentMap,
                seasonSessionIds, buildPriceTypes(), buildRates());

        validate(resultMap);
    }

    private List<ZonaPreciosConfigRecord> buildPriceTypes() {
        ZonaPreciosConfigRecord pt1 = new ZonaPreciosConfigRecord();
        pt1.setIdzona(PRICE_TYPE1_ID.intValue());
        ZonaPreciosConfigRecord pt2 = new ZonaPreciosConfigRecord();
        pt2.setIdzona(PRICE_TYPE2_ID.intValue());
        return Arrays.asList(pt1, pt2);
    }

    private List<RateRecord> buildRates() {
        RateRecord rate1 = new RateRecord();
        rate1.setIdTarifa(RATE1_ID.intValue());
        RateRecord rate2 = new RateRecord();
        rate2.setIdTarifa(RATE2_ID.intValue());
        return Arrays.asList(rate1, rate2);
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
        result.setRefundedSeatQuota(buildRefundedSeatQuota(QUOTA1_ID));
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

    private void validate(Map<Long, SessionConditionsMap> resultMap) {
        Assert.notEmpty(resultMap, "Empty map");
        Assert.isTrue(resultMap.entrySet().size() == 4, "Map size incorrect");

        Assert.notEmpty(resultMap.get(SESSION1_ID).getRefundPercentages(), "Empty map");
        Assert.isTrue(resultMap.get(SESSION1_ID).getRefundPercentages().entrySet().size() == 4, "Map size incorrect");

        Assert.notEmpty(resultMap.get(SESSION2_ID).getRefundPercentages(), "Empty map");
        Assert.isTrue(resultMap.get(SESSION2_ID).getRefundPercentages().entrySet().size() == 4, "Map size incorrect");

        Assert.notEmpty(resultMap.get(SESSION3_ID).getRefundPercentages(), "Empty map");
        Assert.isTrue(resultMap.get(SESSION3_ID).getRefundPercentages().entrySet().size() == 4, "Map size incorrect");

        Assert.notEmpty(resultMap.get(SESSION4_ID).getRefundPercentages(), "Empty map");
        Assert.isTrue(resultMap.get(SESSION4_ID).getRefundPercentages().entrySet().size() == 4, "Map size incorrect");

        Double totalPercentage = resultMap.get(SESSION1_ID).getRefundPercentages().get(PRICE_TYPE1_ID + "_" + RATE1_ID).getRefundPercentage() +
                resultMap.get(SESSION2_ID).getRefundPercentages().get(PRICE_TYPE1_ID + "_" + RATE1_ID).getRefundPercentage() +
                resultMap.get(SESSION3_ID).getRefundPercentages().get(PRICE_TYPE1_ID + "_" + RATE1_ID).getRefundPercentage() +
                resultMap.get(SESSION4_ID).getRefundPercentages().get(PRICE_TYPE1_ID + "_" + RATE1_ID).getRefundPercentage();
        Assert.isTrue(totalPercentage == 100, "Total is not 100");
    }

    private SessionRefundedSeatQuotaDTO buildRefundedSeatQuota(Long id) {
        SessionRefundedSeatQuotaDTO result = new SessionRefundedSeatQuotaDTO();
        result.setId(id);
        result.setEnabled(true);
        return result;
    }
}
