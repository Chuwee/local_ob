package es.onebox.mgmt.sessions.converters;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.TicketStatus;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceType;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceTypeAndRateCondition;
import es.onebox.mgmt.datasources.ms.event.dto.session.Rate;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionConditions;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionRefundConditions;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionRefundedSeatQuota;
import es.onebox.mgmt.sessions.dto.PricePercentageValuesDTO;
import es.onebox.mgmt.sessions.dto.PricePercentageValuesUpdateDTO;
import es.onebox.mgmt.sessions.dto.RateDTO;
import es.onebox.mgmt.sessions.dto.SessionPackRefundConditionsDTO;
import es.onebox.mgmt.sessions.dto.SessionPackRefundConditionsUpdateDTO;
import es.onebox.mgmt.sessions.dto.SessionRefundConditionsDTO;
import es.onebox.mgmt.sessions.dto.SessionRefundConditionsUpdateDTO;
import es.onebox.mgmt.sessions.dto.SessionRefundedSeatQuotaDTO;
import es.onebox.mgmt.sessions.enums.SessionRefundConditionsTicketStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class SessionRefundConditionsConverter {

    private static final String KEY_SEPARATOR = "_";

    private SessionRefundConditionsConverter() {
    }


    public static SessionRefundConditionsDTO convert(SessionRefundConditions source, List<Session> sessionPackSessions,
                                                     List<PriceType> priceTypes, List<Rate> rates) {
        if (source == null) {
            return null;
        }
        SessionRefundConditionsDTO target = new SessionRefundConditionsDTO();
        target.setPrintRefundPrice(source.getPrintRefundPrice());
        target.setSeasonPackAutomaticCalculateConditions(source.getSeasonPackAutomaticCalculateConditions());
        target.setRefundedSeatStatus(SessionRefundConditionsTicketStatus.byTicketStatus(source.getRefundedSeatStatus()));
        target.setRefundedSeatBlockReasonId(source.getRefundedSeatBlockReasonId());
        target.setRefundedSeatQuota(buildSessionRefundedSeatQuotaDTO(source.getRefundedSeatQuota()));
        if (source.getSessionPackRefundConditions() != null) {
            target.setSessionPackRefundConditions(fillSessionPackRefundConditions(source.getSessionPackRefundConditions(),
                    sessionPackSessions, priceTypes, rates));
        }
        return target;
    }


    private static List<SessionPackRefundConditionsDTO> fillSessionPackRefundConditions(
            Map<Long, SessionConditions> seasonPassRefundConditions, List<Session> sessionPackSessions,
            List<PriceType> priceTypes, List<Rate> rates) {

        Map<Long, Session> sessionsById = sessionPackSessions.stream()
                .collect(Collectors.toMap(Session::getId, Function.identity()));

        Map<Long, PriceType> priceTypesById = priceTypes.stream()
                .collect(Collectors.toMap(PriceType::getId, Function.identity()));

        Map<Long, Rate> ratesById = rates.stream()
                .collect(Collectors.toMap(Rate::getId, Function.identity()));

        return seasonPassRefundConditions.keySet().stream()
                .map(sessionConditions -> fillSession(sessionsById.get(sessionConditions)))
                .peek(s -> s.setPricePercentageValues(
                        fillPricePercentageValues(seasonPassRefundConditions.get(s.getSessionId()), priceTypesById, ratesById)))
                .collect(Collectors.toList());
    }

    private static SessionPackRefundConditionsDTO fillSession(Session source) {
        SessionPackRefundConditionsDTO target = new SessionPackRefundConditionsDTO();
        target.setSessionId(source.getId());
        target.setSessionName(source.getName());
        target.setSessionStartDate(source.getDate().getStart());
        return target;
    }

    private static List<PricePercentageValuesDTO> fillPricePercentageValues(SessionConditions sessionConditions,
                                                                            Map<Long, PriceType> priceTypes,
                                                                            Map<Long, Rate> rates) {
        List<PricePercentageValuesDTO> result = new ArrayList<>();
        for (PriceTypeAndRateCondition ptrc : sessionConditions.getRefundPercentages().values()) {
            PricePercentageValuesDTO ppv = new PricePercentageValuesDTO();
            ppv.setPriceType(fillPriceType(priceTypes.get(ptrc.getPriceTypeId())));
            ppv.setRate(fillRate(rates.get(ptrc.getRateId())));
            ppv.setValue(ptrc.getRefundPercentage());
            result.add(ppv);
        }
        return result;
    }

    private static IdNameDTO fillPriceType(PriceType source) {
        IdNameDTO target = new IdNameDTO();
        target.setId(source.getId());
        target.setName(source.getName());
        return target;
    }

    private static RateDTO fillRate(Rate source) {
        RateDTO target = new RateDTO();
        target.setId(source.getId());
        target.setName(source.getName());
        return target;
    }

    public static SessionRefundConditions convert(final SessionRefundConditionsUpdateDTO sessionRefundConditionsDTO) {
        SessionRefundConditions result = new SessionRefundConditions();
        result.setPrintRefundPrice(sessionRefundConditionsDTO.getPrintRefundPrice());
        result.setRefundedSeatBlockReasonId(sessionRefundConditionsDTO.getRefundedSeatBlockReasonId());
        result.setRefundedSeatQuota(buildSessionRefundedSeatQuota(sessionRefundConditionsDTO.getRefundedSeatQuota()));
        if(nonNull(sessionRefundConditionsDTO.getRefundedSeatStatus())){
            result.setRefundedSeatStatus(TicketStatus.getByRefundConditionsTicketStatus(sessionRefundConditionsDTO.getRefundedSeatStatus()));
        }
        result.setSeasonPackAutomaticCalculateConditions(sessionRefundConditionsDTO.getSeasonPackAutomaticCalculateConditions());
        if(CollectionUtils.isNotEmpty(sessionRefundConditionsDTO.getSessionPackRefundConditions())){
            result.setSessionPackRefundConditions(buildSessionPackRefundConditions(sessionRefundConditionsDTO.getSessionPackRefundConditions()));
        }
        return result;
    }

    private static Map<Long, SessionConditions> buildSessionPackRefundConditions(
            final List<SessionPackRefundConditionsUpdateDTO> sessionsRefundConditionsDTO) {

        return sessionsRefundConditionsDTO.stream()
                .collect(Collectors.toMap(SessionPackRefundConditionsUpdateDTO::getSessionId, SessionRefundConditionsConverter::buildSessionConditions));
    }

    private static SessionConditions buildSessionConditions(final SessionPackRefundConditionsUpdateDTO sessionConditionsDTO) {
        Map<String,PriceTypeAndRateCondition> pricePercentages = sessionConditionsDTO.getPricePercentageValues().stream()
                .collect(Collectors.toMap(o -> buildKey(o.getPriceTypeId(),o.getRateId()),
                        SessionRefundConditionsConverter::buildPriceTypeAndRateCondition));

        SessionConditions result = new SessionConditions();
        result.setId(sessionConditionsDTO.getSessionId());
        result.setRefundPercentages(pricePercentages);
        return result;
    }

    private static PriceTypeAndRateCondition buildPriceTypeAndRateCondition(
            final PricePercentageValuesUpdateDTO percentageValueDTO) {

        PriceTypeAndRateCondition result = new PriceTypeAndRateCondition();
        result.setRefundPercentage(percentageValueDTO.getValue());
        result.setRateId(percentageValueDTO.getRateId());
        result.setPriceTypeId(percentageValueDTO.getPriceTypeId());
        return result;
    }

    private static String buildKey(final Long priceTypeId, final Long rateId){
        StringBuilder sb = new StringBuilder();
        return sb.append(priceTypeId).append(KEY_SEPARATOR).append(rateId).toString();
    }

    private static SessionRefundedSeatQuota buildSessionRefundedSeatQuota(final SessionRefundedSeatQuotaDTO refundedSeatQuota) {
        if(isNull(refundedSeatQuota)){
            return null;
        }
        SessionRefundedSeatQuota result = new SessionRefundedSeatQuota();
        result.setEnabled(refundedSeatQuota.getEnabled());
        result.setId(refundedSeatQuota.getId());
        return result;
    }

    private static SessionRefundedSeatQuotaDTO buildSessionRefundedSeatQuotaDTO(final SessionRefundedSeatQuota refundedSeatQuota) {
        if(isNull(refundedSeatQuota)){
            return null;
        }
        SessionRefundedSeatQuotaDTO result = new SessionRefundedSeatQuotaDTO();
        result.setEnabled(refundedSeatQuota.getEnabled());
        result.setId(refundedSeatQuota.getId());
        return result;
    }
}
