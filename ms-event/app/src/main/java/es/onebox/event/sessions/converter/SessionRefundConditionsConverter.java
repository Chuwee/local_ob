package es.onebox.event.sessions.converter;

import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.sessions.domain.sessionconfig.SessionRefundConditions;
import es.onebox.event.sessions.domain.sessionconfig.SessionRefundedSeatQuota;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.PriceTypeAndRateCondition;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.SessionConditionsMap;
import es.onebox.event.sessions.dto.PriceTypeAndRateConditionDTO;
import es.onebox.event.sessions.dto.SessionConditionsDTO;
import es.onebox.event.sessions.dto.SessionRefundConditionsDTO;
import es.onebox.event.sessions.dto.SessionRefundedSeatQuotaDTO;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

public class SessionRefundConditionsConverter {

    private SessionRefundConditionsConverter() {
    }

    public static SessionRefundConditionsDTO convert(SessionRefundConditions source) {
        if (source == null) {
            return null;
        }
        SessionRefundConditionsDTO target = new SessionRefundConditionsDTO();
        target.setPrintRefundPrice(source.getPrintRefundPrice());
        target.setRefundedSeatStatus(source.getRefundedSeatStatus());
        target.setRefundedSeatBlockReasonId(source.getRefundedSeatBlockReasonId());
        target.setRefundedSeatQuota(convertSessionRefundedSeatQuotaDTO(source.getRefundedSeatQuota()));
        target.setSeasonPackAutomaticCalculateConditions(source.getSeasonPackAutomaticCalculateConditions());
        target.setSessionPackRefundConditions(convertSeasonPassRefundConditionsToDTO(source.getSeasonPassRefundConditions()));
        target.setRefundedSessionPackSeatBlockReasonId(source.getRefundedSessionPackSeatBlockReasonId());
        return target;
    }

    public static SessionRefundConditions convert(SessionRefundConditionsDTO source) {
        if (source == null) {
            return null;
        }
        SessionRefundConditions target = new SessionRefundConditions();
        target.setPrintRefundPrice(source.getPrintRefundPrice());
        target.setRefundedSeatStatus(source.getRefundedSeatStatus());
        target.setRefundedSeatBlockReasonId(source.getRefundedSeatBlockReasonId());
        target.setRefundedSeatQuota(convertSessionRefundedSeatQuota(source.getRefundedSeatQuota()));
        target.setSeasonPackAutomaticCalculateConditions(source.getSeasonPackAutomaticCalculateConditions());
        target.setSeasonPassRefundConditions(convertSeasonPassRefundConditions(source.getSessionPackRefundConditions()));
        target.setRefundedSessionPackSeatBlockReasonId(source.getRefundedSessionPackSeatBlockReasonId());
        return target;
    }

    private static Map<Long, SessionConditionsDTO> convertSeasonPassRefundConditionsToDTO(Map<Long, SessionConditionsMap> source) {
        if (source == null) {
            return null;
        }
        return source.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> convertSessionConditions(e.getValue())
                ));
    }

    private static SessionConditionsDTO convertSessionConditions(SessionConditionsMap source) {
        if (source == null) {
            return null;
        }
        SessionConditionsDTO target = new SessionConditionsDTO();
        target.setId(source.getId());
        target.setRefundPercentages(convertRefundPercentagesToDTO(source.getRefundPercentages()));
        return target;
    }

    private static Map<String, PriceTypeAndRateConditionDTO> convertRefundPercentagesToDTO(Map<String, PriceTypeAndRateCondition> source) {
        if (source == null) {
            return null;
        }
        return source.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> convertPriceTypeAndRateConditions(e.getValue())
                ));
    }

    private static PriceTypeAndRateConditionDTO convertPriceTypeAndRateConditions(PriceTypeAndRateCondition source) {
        if (source == null) {
            return null;
        }
        PriceTypeAndRateConditionDTO target = new PriceTypeAndRateConditionDTO();
        target.setPriceTypeId(source.getPriceTypeId());
        target.setRateId(source.getRateId());
        target.setRefundPercentage(source.getRefundPercentage());
        return target;
    }


    private static Map<Long, SessionConditionsMap> convertSeasonPassRefundConditions(Map<Long, SessionConditionsDTO> source) {
        if (source == null) {
            return null;
        }
        return source.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> convertSessionConditions(e.getValue())
                ));
    }

    private static SessionConditionsMap convertSessionConditions(SessionConditionsDTO source) {
        if (source == null) {
            return null;
        }
        SessionConditionsMap target = new SessionConditionsMap();
        target.setId(source.getId());
        target.setRefundPercentages(convertRefundPercentages(source.getRefundPercentages()));
        return target;
    }

    private static Map<String, PriceTypeAndRateCondition> convertRefundPercentages(Map<String, PriceTypeAndRateConditionDTO> source) {
        if (source == null) {
            return null;
        }
        return source.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> convertPriceTypeAndRateConditions(e.getValue())
                ));
    }

    private static PriceTypeAndRateCondition convertPriceTypeAndRateConditions(PriceTypeAndRateConditionDTO source) {
        if (source == null) {
            return null;
        }
        PriceTypeAndRateCondition target = new PriceTypeAndRateCondition();
        target.setPriceTypeId(source.getPriceTypeId());
        target.setRateId(source.getRateId());
        target.setRefundPercentage(source.getRefundPercentage());
        return target;
    }

    public static SessionRefundConditions mergeToEntity(SessionRefundConditions newEntity,
                                                        SessionRefundConditions currentEntity) {

        SessionRefundConditions result = new SessionRefundConditions();
        BeanUtils.copyProperties(currentEntity,result);

        if(nonNull(newEntity.getRefundedSeatQuota())) {
            result.setRefundedSeatQuota(newEntity.getRefundedSeatQuota());
        }
        if(nonNull(newEntity.getRefundedSeatBlockReasonId())) {
            result.setRefundedSeatBlockReasonId(newEntity.getRefundedSeatBlockReasonId());
        }
        if(nonNull(newEntity.getRefundedSeatStatus())) {
            if(!TicketStatus.BLOCKED_PROMOTER.equals(newEntity.getRefundedSeatStatus())){
                result.setRefundedSeatBlockReasonId(null);
            }
            result.setRefundedSeatStatus(newEntity.getRefundedSeatStatus());
        }
        if(nonNull(newEntity.getPrintRefundPrice())) {
            result.setPrintRefundPrice(newEntity.getPrintRefundPrice());
        }
        if(nonNull(newEntity.getSeasonPackAutomaticCalculateConditions())) {
            result.setSeasonPackAutomaticCalculateConditions(newEntity.getSeasonPackAutomaticCalculateConditions());
        }

        if(isTrue(newEntity.getSeasonPackAutomaticCalculateConditions())) {
            result.setSeasonPassRefundConditions(newEntity.getSeasonPassRefundConditions());
        }else if(MapUtils.isNotEmpty(newEntity.getSeasonPassRefundConditions())) {

            Map<Long, SessionConditionsMap> newRefundConditions = newEntity.getSeasonPassRefundConditions();
            Map<Long, SessionConditionsMap> resultRefundConditions = result.getSeasonPassRefundConditions();

            newRefundConditions.entrySet().stream().forEach(sessionEntry -> {
                Long sessionId = sessionEntry.getKey();
                sessionEntry.getValue().getRefundPercentages().entrySet().stream().forEach(percentageEntry -> {
                    String priceTypeAndRateId = percentageEntry.getKey();
                    Double value = percentageEntry.getValue().getRefundPercentage();
                    resultRefundConditions.get(sessionId).getRefundPercentages().get(priceTypeAndRateId)
                            .setRefundPercentage(value);
                });
            });

            result.setSeasonPassRefundConditions(resultRefundConditions);
        }

        return result;
    }

    private static SessionRefundedSeatQuota convertSessionRefundedSeatQuota(final SessionRefundedSeatQuotaDTO refundedSeatQuota) {
        if(isNull(refundedSeatQuota)){
            return null;
        }

        SessionRefundedSeatQuota result = new SessionRefundedSeatQuota();
        result.setEnabled(refundedSeatQuota.getEnabled());
        result.setId(refundedSeatQuota.getId());
        return result;
    }

    private static SessionRefundedSeatQuotaDTO convertSessionRefundedSeatQuotaDTO(final SessionRefundedSeatQuota refundedSeatQuota) {
        if(isNull(refundedSeatQuota)){
            return null;
        }

        SessionRefundedSeatQuotaDTO result = new SessionRefundedSeatQuotaDTO();
        result.setEnabled(refundedSeatQuota.getEnabled());
        result.setId(refundedSeatQuota.getId());
        return result;
    }
}
