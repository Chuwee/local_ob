package es.onebox.mgmt.sessions;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtSessionErrorCode;
import es.onebox.mgmt.sessions.dto.PricePercentageValuesUpdateDTO;
import es.onebox.mgmt.sessions.dto.SessionPackRefundConditionsUpdateDTO;
import es.onebox.mgmt.sessions.dto.SessionRefundConditionsBaseDTO;
import es.onebox.mgmt.sessions.dto.SessionRefundConditionsUpdateDTO;
import es.onebox.mgmt.sessions.enums.SessionRefundConditionsTicketStatus;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

@Service
public class SessionRefundConditionsValidationService {

    private ValidationService validationService;

    @Autowired
    public SessionRefundConditionsValidationService(final ValidationService validationService){
        this.validationService = validationService;
    }

    public Session validate(final Long eventId, final Long sessionId) {
        Session session = validationService.getAndCheckSession(eventId, sessionId);
        if (!SessionType.SEASON_FREE.equals(session.getSessionType())) {
            throw ExceptionBuilder.build(ApiMgmtSessionErrorCode.SESSION_PACK_ALLOW_REFUND_CONDITIONS);
        }
        if(BooleanUtils.isFalse(session.getAllowPartialRefund())){
            throw ExceptionBuilder.build(ApiMgmtSessionErrorCode.SESSION_PACK_NOT_ALLOW_PARTIAL_REFUND);
        }
        return session;
    }

    public void validate(final SessionRefundConditionsUpdateDTO sessionRefundConditionsDTO) {
        if(isNull(sessionRefundConditionsDTO)){
            throw ExceptionBuilder.build(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }

        List<SessionPackRefundConditionsUpdateDTO> sessionsRefundConditionsDTO =
                sessionRefundConditionsDTO.getSessionPackRefundConditions();

        this.validateBaseRefundConditions(sessionRefundConditionsDTO);
        this.validateInvalidValues(sessionsRefundConditionsDTO);
    }

    private void validateBaseRefundConditions(final SessionRefundConditionsBaseDTO sessionRefundConditionsBase){
        if(SessionRefundConditionsTicketStatus.PROMOTOR_LOCKED.equals(sessionRefundConditionsBase.getRefundedSeatStatus()) &&
                isNullOrNegative(sessionRefundConditionsBase.getRefundedSeatBlockReasonId())){
            throw ExceptionBuilder.build(ApiMgmtSessionErrorCode.SESSION_CREATE_PACK_BLOCKING_REASON);
        }
    }

    private void validateInvalidValues(final List<SessionPackRefundConditionsUpdateDTO> sessionsRefundConditionsDTO) {
        if(CollectionUtils.isEmpty(sessionsRefundConditionsDTO)){
            return;
        }

        sessionsRefundConditionsDTO.stream()
                .map(this::validate)
                .map(SessionPackRefundConditionsUpdateDTO::getPricePercentageValues)
                .flatMap(List::stream)
                .forEach(this::validate);
    }

    private SessionPackRefundConditionsUpdateDTO validate(final SessionPackRefundConditionsUpdateDTO sessionConditions) {
        if(isNullOrNegative(sessionConditions.getSessionId()) ||
                CollectionUtils.isEmpty(sessionConditions.getPricePercentageValues())) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }
        return sessionConditions;
    }

    private void validate(final PricePercentageValuesUpdateDTO pricePercentage) {
        if(isNull(pricePercentage) || isNullOrNegative(pricePercentage.getPriceTypeId()) ||
                isNullOrNegative(pricePercentage.getRateId()) || isInvalidPercentage(pricePercentage.getValue())){
            throw ExceptionBuilder.build(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }
    }

    private boolean isNullOrNegative(final Long number){
        return isNull(number) || isNegative(number);
    }

    private boolean isNegative(final Long number){
        return number.longValue() < 0;
    }

    private boolean isInvalidPercentage(final Double number){
        return number.compareTo(0D) < 0 || number.compareTo(100D) > 0;
    }
}
