package es.onebox.event.sessions.utils;

import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.sessions.dao.record.ZonaPreciosConfigRecord;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.PriceTypeAndRateCondition;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.SessionConditionsMap;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

public class RefundConditionsUtils {

    private static final String KEY_SEPARATOR = "_";

    private RefundConditionsUtils(){
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static Map<Long, SessionConditionsMap> buildRefundConditionsMap(Map<Long, SessionConditionsMap> currentMap,
                                                                           List<Long> seasonSessionIds,
                                                                           List<ZonaPreciosConfigRecord> priceTypes,
                                                                           Collection<RateRecord> rates) {
        Integer numSessions = seasonSessionIds.size();
        Long lastSessionId = seasonSessionIds.get(numSessions-1);

        BigDecimal refundPercentage = BigDecimal.valueOf(100).divide(BigDecimal.valueOf(numSessions),2, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal lastRefundPercentage = BigDecimal.valueOf(100).subtract((refundPercentage.multiply(BigDecimal.valueOf(numSessions-1))))
                .setScale(2, RoundingMode.HALF_UP);

        return seasonSessionIds.stream().collect(Collectors.toMap(Function.identity(),
                id -> buildSessionConditionsMap(id, priceTypes, rates, lastSessionId,refundPercentage.doubleValue(),
                        lastRefundPercentage.doubleValue(),currentMap)));
    }

    private static SessionConditionsMap buildSessionConditionsMap(Long sessionId, List<ZonaPreciosConfigRecord> priceTypes,
                                                           Collection<RateRecord> rates, Long lastSessionId,
                                                           Double refundPercentage, Double lastRefundPercentage,
                                                                  Map<Long, SessionConditionsMap> currentMap) {

        Double percentage = lastSessionId.equals(sessionId) ? lastRefundPercentage : refundPercentage;

        Map<String, PriceTypeAndRateCondition> refundPercentagesMap = buildPriceTypeAndRateConditions(priceTypes,rates,
                percentage, sessionId, currentMap);

        SessionConditionsMap sessionConditionsMap = new SessionConditionsMap();
        sessionConditionsMap.setId(sessionId);
        sessionConditionsMap.setRefundPercentages(refundPercentagesMap);
        return sessionConditionsMap;
    }

    private static Map<String, PriceTypeAndRateCondition> buildPriceTypeAndRateConditions(List<ZonaPreciosConfigRecord> priceTypes,
                                                                Collection<RateRecord> rates, Double percentage, Long sessionId,
                                                                Map<Long, SessionConditionsMap> currentMap){

        return priceTypes.stream()
                .map(priceType -> getRatesByPriceType(priceType,rates,percentage,sessionId,currentMap))
                .flatMap(List<PriceTypeAndRateCondition>::stream)
                .collect(Collectors.toMap(RefundConditionsUtils::buildKey, Function.identity()));
    }

    private static List<PriceTypeAndRateCondition> getRatesByPriceType(ZonaPreciosConfigRecord priceType,
                                                            Collection<RateRecord> rates, Double percentage,
                                                            Long sessionId, Map<Long, SessionConditionsMap> currentMap){
        return rates.stream().map(rate -> {
            Double currentPercentage = getPercentageBySessionPriceTypeAndRate(currentMap,sessionId,priceType.getIdzona(),
                    rate.getIdTarifa());

            PriceTypeAndRateCondition obj = new PriceTypeAndRateCondition();
            obj.setPriceTypeId(priceType.getIdzona());
            obj.setRateId(rate.getIdTarifa());
            obj.setRefundPercentage(currentPercentage != null ? currentPercentage : percentage);
            return obj;
        }).collect(Collectors.toList());
    }

    private static String buildKey(PriceTypeAndRateCondition obj) {
        return buildKey(obj.getPriceTypeId(),obj.getRateId());
    }

    private static String buildKey(Integer priceTypeId, Integer rateId) {
        StringBuilder sb = new StringBuilder();
        sb.append(priceTypeId).append(KEY_SEPARATOR).append(rateId);
        return sb.toString();
    }

    public static List<Long> getSessionIds(Map<Long, ?> sessionsRefundConditions) {
        return sessionsRefundConditions.keySet().stream()
                .collect(Collectors.toList());
    }

    public static List<Integer> getPriceTypeIds(Map<?, SessionConditionsMap> sessionsRefundConditions){
        return  getPriceTypeAndRateConditionStream(sessionsRefundConditions)
                .map(PriceTypeAndRateCondition::getPriceTypeId)
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Integer> getRateIds(Map<?, SessionConditionsMap> sessionsRefundConditions){
        return  getPriceTypeAndRateConditionStream(sessionsRefundConditions)
                .map(PriceTypeAndRateCondition::getRateId)
                .distinct()
                .collect(Collectors.toList());
    }

    private static Stream<PriceTypeAndRateCondition> getPriceTypeAndRateConditionStream(
            Map<?, SessionConditionsMap> sessionsRefundConditions) {

        return sessionsRefundConditions.values().stream()
                .map(SessionConditionsMap::getRefundPercentages)
                .map(Map::values)
                .flatMap(Collection::stream);
    }

    public static List<Integer> getZonaPreciosConfigIds(List<ZonaPreciosConfigRecord> zonaPreciosConfigs){
        return zonaPreciosConfigs.stream().map(ZonaPreciosConfigRecord::getIdzona).collect(Collectors.toList());
    }

    public static List<Integer> getRateIds(Collection<RateRecord> rateRecords){
        return rateRecords.stream().map(RateRecord::getIdTarifa).collect(Collectors.toList());
    }

    public static Map<String, Map<Long, Double>> getMapByPriceTypeAndRate(
            Map<Long, SessionConditionsMap> sessionsRefundConditions) {

        List<String> priceTypeAndRateIds = getPriceTypeAndRateIds(sessionsRefundConditions);
        return priceTypeAndRateIds.stream()
                .collect(Collectors.toMap(Function.identity(),
                        o -> RefundConditionsUtils.getSessionPercentagesByPriceTypeAndRate(o,sessionsRefundConditions)));
    }

    private static List<String> getPriceTypeAndRateIds(Map<Long, SessionConditionsMap> sessionsRefundConditions){
        return  sessionsRefundConditions.values().stream()
                .map(SessionConditionsMap::getRefundPercentages)
                .map(Map::keySet)
                .flatMap(Set::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private static Map<Long,Double> getSessionPercentagesByPriceTypeAndRate(String priceTypeAndRateId,
                                                             Map<Long, SessionConditionsMap> sessionsRefundConditions){

        List<Long> sessionIds = getSessionIds(sessionsRefundConditions);
        return sessionIds.stream().collect(Collectors.toMap(Function.identity(),o ->
                sessionsRefundConditions.get(o).getRefundPercentages().get(priceTypeAndRateId).getRefundPercentage()));

    }

    private static Double getPercentageBySessionPriceTypeAndRate(Map<Long, SessionConditionsMap> currentMap,
                                                                 Long sessionId, Integer priceTypeId, Integer rateId) {
        if(MapUtils.isEmpty(currentMap)){
            return null;
        }

        PriceTypeAndRateCondition priceTypeAndRateCondition = currentMap.get(sessionId).getRefundPercentages()
                .get(buildKey(priceTypeId,rateId));

        if(nonNull(priceTypeAndRateCondition)){
            return priceTypeAndRateCondition.getRefundPercentage();
        }
        return null;
    }
}
