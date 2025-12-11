package es.onebox.mgmt.sessions;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.integration.avetconfig.repository.AvetConfigRepository;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

@Service
public class SessionMappingService {

    private final ValidationService validationService;
    private final AvetConfigRepository avetConfigRepository;


    public SessionMappingService(ValidationService validationService, AvetConfigRepository avetConfigRepository) {
        this.validationService = validationService;
        this.avetConfigRepository = avetConfigRepository;
    }

    public void mapCapacity (Long eventId, Long sessionId, Boolean mappingTickets, Boolean mappingFull) {
        Session session = validationService.getAndCheckSession(eventId, sessionId);

        if (!SessionUtils.isAvetEvent(session.getEventType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_IS_NOT_AVET);
        }
        if (session.getStatus().equals(SessionStatus.CANCELLED) || session.getStatus().equals(SessionStatus.FINALIZED)){
            throw new OneboxRestException(ApiMgmtErrorCode.SESSION_NOT_FOUND);
        }

        if ((mappingFull == null && mappingTickets == null) || (BooleanUtils.isTrue(mappingFull) && BooleanUtils.isTrue(mappingTickets))) {
            avetConfigRepository.createSessionMappingFull(sessionId, session.getVenueConfigId());
            avetConfigRepository.createSessionTicketsMappings(sessionId, session.getVenueConfigId());
        } else if (BooleanUtils.isTrue(mappingFull)) {
            avetConfigRepository.createSessionMappingFull(sessionId, session.getVenueConfigId());
        } else if (BooleanUtils.isTrue(mappingTickets)) {
           avetConfigRepository.createSessionTicketsMappings(sessionId, session.getVenueConfigId());
        }
    }
}


