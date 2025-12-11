package es.onebox.mgmt.sessions;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionSaleRestriction;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.exception.ApiMgmtSessionErrorCode;
import es.onebox.mgmt.sessions.converters.SessionSaleRestrictionsConverter;
import es.onebox.mgmt.sessions.dto.SessionSaleRestrictionDTO;
import es.onebox.mgmt.sessions.dto.SessionSaleRestrictionsDTO;
import es.onebox.mgmt.sessions.dto.UpdateSaleRestrictionDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionSaleRestrictionsService {

    private final ValidationService validationService;
    private final SessionsRepository sessionsRepository;

    @Autowired
    public SessionSaleRestrictionsService(ValidationService validationService, SessionsRepository sessionsRepository) {
        this.validationService = validationService;
        this.sessionsRepository = sessionsRepository;
    }

    public void upsertRestriction(Long eventId, Long sessionId, Long lockedPriceTypeId, UpdateSaleRestrictionDTO request) {
        validationService.getAndCheckSession(eventId, sessionId);
        if (CollectionUtils.isEmpty(request.getRequiredPriceTypeIds())) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.REQUIRED_PRICE_TYPE_MANDATORY);
        }
        if(request.getRequiredPriceTypeIds().stream()
                .anyMatch(lockedPriceTypeId::equals)){
            throw new OneboxRestException(ApiMgmtSessionErrorCode.CIRCULAR_PRICE_TYPE_RESTRICTION);
        }
        if ((request.getLockedTicketsNumber() != null && request.getRequiredTicketsNumber() != null) ||
                (request.getLockedTicketsNumber() == null && request.getRequiredTicketsNumber() == null)) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.TICKET_NUMBER_EXCLUSION_INPUT);
        }
        sessionsRepository.upsertSaleRestrictions(eventId, sessionId, lockedPriceTypeId, SessionSaleRestrictionsConverter.convert(request));
    }

    public void deleteRestriction(Long eventId, Long sessionId, Long lockedPriceTypeId) {
        validationService.getAndCheckSession(eventId, sessionId);
        sessionsRepository.deleteRestriction(eventId, sessionId, lockedPriceTypeId);
    }

    public SessionSaleRestrictionDTO getRestriction(Long eventId, Long sessionId, Long lockedPriceTypeId) {
        validationService.getAndCheckSession(eventId, sessionId);
        SessionSaleRestriction saleRestriction = sessionsRepository.getSaleRestrictions(eventId, sessionId, lockedPriceTypeId);
        if (saleRestriction == null) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.PRICE_TYPE_RESTRICTION_NOT_FOUND);
        }
        return SessionSaleRestrictionsConverter.convert(saleRestriction);
    }

    public SessionSaleRestrictionsDTO getSessionRestrictions(Long eventId, Long sessionId) {
        validationService.getAndCheckSession(eventId, sessionId);
        List<IdNameDTO> sessionRestrictions = sessionsRepository.getSessionRestrictions(eventId, sessionId);
        return SessionSaleRestrictionsConverter.convert(sessionRestrictions);
    }

}
