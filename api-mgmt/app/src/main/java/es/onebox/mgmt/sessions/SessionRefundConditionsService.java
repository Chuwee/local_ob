package es.onebox.mgmt.sessions;

import es.onebox.mgmt.datasources.ms.event.dto.session.PriceType;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionRefundConditions;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.converters.SessionRefundConditionsConverter;
import es.onebox.mgmt.sessions.dto.SessionRefundConditionsDTO;
import es.onebox.mgmt.sessions.dto.SessionRefundConditionsUpdateDTO;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static es.onebox.mgmt.sessions.enums.SessionField.ID;
import static es.onebox.mgmt.sessions.enums.SessionField.NAME;
import static es.onebox.mgmt.sessions.enums.SessionField.STARTDATE;

@Service
public class SessionRefundConditionsService {

    private SessionsRepository sessionsRepository;
    private EventsRepository eventsRepository;
    private SessionRefundConditionsValidationService validationService;

    @Autowired
    public SessionRefundConditionsService(final SessionsRepository sessionsRepository,
                                          final EventsRepository eventsRepository,
                                          final SessionRefundConditionsValidationService validationService){
        this.sessionsRepository = sessionsRepository;
        this.eventsRepository = eventsRepository;
        this.validationService = validationService;
    }

    public SessionRefundConditionsDTO getSessionRefundConditions(final Long eventId, final Long sessionId) {
        Session session = validationService.validate(eventId,sessionId);
        SessionRefundConditions src = sessionsRepository.getSessionRefundConditions(eventId, sessionId);
        List<Long> sessionIds = SessionRefundConditionsUtils.getSessionIds(src.getSessionPackRefundConditions());
        List<Session> sessionPackSessions = null;
        List<PriceType> priceTypes = null;
        if (sessionIds != null) {
            SessionSearchFilter filter = new SessionSearchFilter();
            filter.setId(sessionIds);
            filter.setFields(List.of(ID.name(), NAME.name(), STARTDATE.name()));
            sessionPackSessions = eventsRepository.getSessions(SecurityUtils.getUserOperatorId(), eventId, filter)
                    .getData();

            priceTypes = sessionsRepository.getPriceTypes(eventId, sessionId).getData();

        }
        return SessionRefundConditionsConverter.convert(src, sessionPackSessions, priceTypes, session.getRates());
    }

    public void updateSessionRefundConditions(final Long eventId, final Long sessionId, final SessionRefundConditionsUpdateDTO updateRequest) {
        validationService.validate(eventId, sessionId);
        validationService.validate(updateRequest);

        SessionRefundConditions sessionRefundConditions = SessionRefundConditionsConverter.convert(updateRequest);
        sessionsRepository.updateSessionRefundConditions(eventId,sessionId,sessionRefundConditions);
    }



}
