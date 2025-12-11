package es.onebox.event.sessions.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.ms.venue.dto.QuotaDTO;
import es.onebox.event.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dao.record.ZonaPreciosConfigRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionRefundConditions;
import es.onebox.event.sessions.domain.sessionconfig.SessionRefundedSeatQuota;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.PriceTypeAndRateCondition;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.SessionConditionsMap;
import es.onebox.event.sessions.utils.RefundConditionsUtils;
import es.onebox.event.venues.dao.BlockingReasonDao;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.jooq.exception.PersistenceException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.onebox.event.sessions.utils.RefundConditionsUtils.getPriceTypeIds;
import static es.onebox.event.sessions.utils.RefundConditionsUtils.getRateIds;
import static es.onebox.event.sessions.utils.RefundConditionsUtils.getSessionIds;
import static es.onebox.event.sessions.utils.RefundConditionsUtils.getZonaPreciosConfigIds;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class SessionRefundConditionsValidationService {

    private final PriceTypeConfigDao priceZoneConfigDao;
    private final RateDao rateDao;
    private final VenuesRepository venuesRepository;
    private final BlockingReasonDao blockingReasonDao;

    @Autowired
    public SessionRefundConditionsValidationService(final PriceTypeConfigDao priceZoneConfigDao, final RateDao rateDao,
                                                    final BlockingReasonDao blockingReasonDao,
                                                    final VenuesRepository venuesRepository) {
        this.priceZoneConfigDao = priceZoneConfigDao;
        this.rateDao = rateDao;
        this.blockingReasonDao = blockingReasonDao;
        this.venuesRepository = venuesRepository;
    }

    public void validate(final SessionRefundConditions currentEntity, final SessionRefundConditions newEntity,
                         final SessionRecord session) {

        this.validateSeatStatusAndBlockingReason(newEntity.getRefundedSeatStatus(),
                newEntity.getRefundedSeatBlockReasonId(), session.getVenueTemplateId());

        if(nonNull(newEntity.getRefundedSeatQuota())) {
            this.validateQuota(session.getVenueTemplateId().longValue(), newEntity.getRefundedSeatQuota());
        }

        if(isNotTrue(newEntity.getSeasonPackAutomaticCalculateConditions()) &&
                MapUtils.isNotEmpty(newEntity.getSeasonPassRefundConditions())) {

            this.validateMatrix(session, newEntity.getSeasonPassRefundConditions(),
                    currentEntity.getSeasonPassRefundConditions());
        }
    }

    private void validateMatrix(final SessionRecord session, final Map<Long, SessionConditionsMap> inputMap,
                               final Map<Long, SessionConditionsMap> currentMap){

        this.validateSessions(inputMap, currentMap);
        this.validatePriceTypes(session, inputMap);
        this.validateRates(session, inputMap);
        this.validatePercentages(inputMap, currentMap);
    }

    private void validateSessions(final Map<Long, SessionConditionsMap> newMap,
                                  final Map<Long, SessionConditionsMap> currentMap) {

        List<Long> sessionIds = getSessionIds(currentMap);
        List<Long> newSessionIds = getSessionIds(newMap);

        boolean validated = newSessionIds.stream().allMatch(id -> sessionIds.contains(id));
        if(!validated){
            throw ExceptionBuilder.build(MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_SESSIONS);
        }
    }

    private void validatePriceTypes(final SessionRecord session, final Map<Long, SessionConditionsMap> newMap) {
        List<ZonaPreciosConfigRecord> cpanelZonaPreciosConfigRecords = this.getZonaPreciosConfigRecords(session);

        List<Integer> zonaPreciosConfigIds = getZonaPreciosConfigIds(cpanelZonaPreciosConfigRecords);
        List<Integer> priceTypeIds = getPriceTypeIds(newMap);

        boolean validated = priceTypeIds.stream().allMatch(id -> zonaPreciosConfigIds.contains(id));
        if(!validated){
            throw ExceptionBuilder.build(MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_PRICETYPES);
        }
    }

    private List<ZonaPreciosConfigRecord> getZonaPreciosConfigRecords(final SessionRecord session) {
        Long venueConfigId = session.getVenueTemplateId().longValue();
        if (BooleanUtils.isTrue(session.getUsaaccesosplantilla())) {
            return priceZoneConfigDao.getPriceZone(venueConfigId, null);
        } else {
            return priceZoneConfigDao.getPriceZoneBySession(venueConfigId, null, session.getIdsesion().longValue());
        }
    }

    private void validateRates(final SessionRecord session, final Map<Long, SessionConditionsMap> newMap) {
        Collection<RateRecord> rateRecords = rateDao.getRatesBySessionId(session.getIdsesion(), 1000L, 0L);

        List<Integer> rateIds = getRateIds(rateRecords);
        List<Integer> newRateIds = getRateIds(newMap);

        boolean validated = newRateIds.stream().allMatch(id -> rateIds.contains(id));
        if(!validated){
            throw ExceptionBuilder.build(MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_RATES);
        }
    }

    private void validatePercentages(final Map<Long, SessionConditionsMap> newMap,
                                     final Map<Long, SessionConditionsMap> currentMap) {
        validatePercentageValues(newMap);
        validateTotalPercentagesByPriceTypeAndRate(newMap,currentMap);
    }

    private void validatePercentageValues(final Map<Long, SessionConditionsMap> map) {
        boolean validated = map.values().stream()
                .map(SessionConditionsMap::getRefundPercentages)
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(PriceTypeAndRateCondition::getRefundPercentage)
                .allMatch(percentage ->
                        nonNull(percentage) && percentage.compareTo(0D) >= 0 && percentage.compareTo(100D) <= 0);
        if(!validated){
            throw ExceptionBuilder.build(MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_PERCENTAGE);
        }
    }

    private void validateTotalPercentagesByPriceTypeAndRate(final Map<Long, SessionConditionsMap> newMap,
                                                            final Map<Long, SessionConditionsMap> currentMap) {

        Map<String, Map<Long, Double>> currentMapByPriceTypeAndRate =
                RefundConditionsUtils.getMapByPriceTypeAndRate(currentMap);
        Map<String, Map<Long, Double>> newMapByPriceTypeAndRate =
                RefundConditionsUtils.getMapByPriceTypeAndRate(newMap);

        boolean validated = newMapByPriceTypeAndRate.entrySet().stream().allMatch(entry -> {
            String priceTypeAndRateId = entry.getKey();
            Map<Long, Double> newSessionPercentagesMap = entry.getValue();
            Map<Long, Double> currentSessionPercentagesMap = currentMapByPriceTypeAndRate.get(priceTypeAndRateId);

            BigDecimal total = currentSessionPercentagesMap.entrySet().stream()
                    .map(o -> {
                        if (newSessionPercentagesMap.containsKey(o.getKey())) {
                            return newSessionPercentagesMap.get(o.getKey());
                        }
                        return o.getValue();
                    })
                    .map(BigDecimal::valueOf)
                    .reduce(BigDecimal.ZERO,
                            (subtotal, value) -> subtotal.add(value).setScale(2, RoundingMode.HALF_UP));

            return total.compareTo(BigDecimal.valueOf(100)) == 0;
        });

        if (!validated) {
            throw ExceptionBuilder.build(MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_TOTAL_PERCENTAGE);
        }
    }

    private void validateQuota(final Long venueTemplateId,final SessionRefundedSeatQuota refundedSeatQuota) {
        if(isTrue(refundedSeatQuota.getEnabled())) {
            if(isNull(refundedSeatQuota.getId())){
                throw ExceptionBuilder.build(MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_QUOTA);
            }

            List<QuotaDTO> quotaCapacities = venuesRepository.getQuotas(venueTemplateId);
            List<Long> quotas = quotaCapacities.stream()
                    .map(QuotaDTO::getId)
                    .collect(Collectors.toList());

            if(CollectionUtils.isEmpty(quotas) || !quotas.contains(refundedSeatQuota.getId())) {
                throw ExceptionBuilder.build(MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_QUOTA);
            }
        }
    }

    private void validateSeatStatusAndBlockingReason(final TicketStatus seatStatus, final Long blockingReasonId,
                                                     final Integer venueTemplateId) {
        if(nonNull(blockingReasonId) && isNull(seatStatus)){
            throw ExceptionBuilder.build(MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_SEAT_STATUS);
        }

        if(nonNull(seatStatus)){
            if(TicketStatus.BLOCKED_PROMOTER.equals(seatStatus)){
                if(isNull(blockingReasonId)) {
                    throw ExceptionBuilder.build(MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_BLOCKING_REASON);
                }

                try{
                    List<Long> venueTemplateBlockingReasons = blockingReasonDao.findByVenueTemplate(venueTemplateId);
                    if(CollectionUtils.isEmpty(venueTemplateBlockingReasons) ||
                            !venueTemplateBlockingReasons.contains(blockingReasonId)){
                        throw ExceptionBuilder.build(MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_BLOCKING_REASON);
                    }
                }catch(PersistenceException ex) {
                    throw ExceptionBuilder.build(MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_INVALID_BLOCKING_REASON);
                }
            }
        }
    }

}
