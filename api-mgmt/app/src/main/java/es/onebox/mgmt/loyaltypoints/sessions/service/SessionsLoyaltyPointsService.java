package es.onebox.mgmt.loyaltypoints.sessions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.loyaltypoints.sessions.converter.SessionsLoyaltyPointsConverter;
import es.onebox.mgmt.loyaltypoints.sessions.dto.LoyaltyPointsConfigDTO;
import es.onebox.mgmt.loyaltypoints.sessions.dto.UpdateLoyaltyPointsConfigDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;

@Service
public class SessionsLoyaltyPointsService {

    private final EventsRepository eventsRepository;
    private final EntitiesRepository entitiesRepository;
    private final ValidationService validationService;

    @Autowired
    public SessionsLoyaltyPointsService(EventsRepository eventsRepository, EntitiesRepository entitiesRepository,
                                        ValidationService validationService) {
        this.eventsRepository = eventsRepository;
        this.entitiesRepository = entitiesRepository;
        this.validationService = validationService;
    }

    public LoyaltyPointsConfigDTO getLoyaltyPointsConfig(Long eventId, Long sessionId) {
        return SessionsLoyaltyPointsConverter.toDTO(eventsRepository.getLoyaltyPointsConfig(eventId, sessionId));
    }

    public void updateLoyaltyPointsConfig(Long eventId, Long sessionId, UpdateLoyaltyPointsConfigDTO updateLoyaltyPointsConfig) {
        Event event = validationService.getAndCheckEvent(eventId);
        validateLoyaltyPointsConfig(event.getEntityId());
        eventsRepository.updateLoyaltyPointsConfig(eventId, sessionId, SessionsLoyaltyPointsConverter.toMs(updateLoyaltyPointsConfig));
    }

    private void validateLoyaltyPointsConfig(Long entityId) {
        Entity entity = entitiesRepository.getEntity(entityId);

        if (BooleanUtils.isNotTrue(entity.getAllowLoyaltyPoints())) {
            throw new OneboxRestException(ApiMgmtErrorCode.LOYALTY_POINTS_NOT_ALLOWED);
        }
    }
}
