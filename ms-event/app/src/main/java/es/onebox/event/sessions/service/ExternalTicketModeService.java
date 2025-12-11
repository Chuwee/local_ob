package es.onebox.event.sessions.service;

import es.onebox.event.datasources.integration.avet.config.dto.ClubConfig;
import es.onebox.event.datasources.integration.avet.config.repository.IntAvetConfigRepository;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.domain.eventconfig.EventExternalConfig;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.domain.sessionconfig.DigitalTicketMode;
import es.onebox.event.sessions.domain.sessionconfig.SessionExternalConfig;
import org.springframework.stereotype.Service;

@Service
public class ExternalTicketModeService {

    private final SessionConfigCouchDao sessionConfigCouchDao;
    private final EventConfigCouchDao eventConfigCouchDao;
    private final IntAvetConfigRepository intAvetConfigRepository;

    public ExternalTicketModeService(SessionConfigCouchDao sessionConfigCouchDao, EventConfigCouchDao eventConfigCouchDao,
                                     IntAvetConfigRepository intAvetConfigRepository) {

        this.sessionConfigCouchDao = sessionConfigCouchDao;
        this.eventConfigCouchDao = eventConfigCouchDao;
        this.intAvetConfigRepository = intAvetConfigRepository;
    }

    public DigitalTicketMode getDigitalTicketMode(Integer entityId, Long eventId, Long sessionId) {
        SessionExternalConfig sessionExternalConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId).getSessionExternalConfig();
        if (sessionExternalConfig != null && sessionExternalConfig.getDigitalTicketMode() != null) {
            return sessionExternalConfig.getDigitalTicketMode();
        } else {
            EventExternalConfig eventExternalConfig = eventConfigCouchDao.getOrInitEventConfig(eventId).getEventExternalConfig();
            if (eventExternalConfig != null && eventExternalConfig.getDigitalTicketMode() != null) {
                return eventExternalConfig.getDigitalTicketMode();
            } else {
                ClubConfig clubConfig = intAvetConfigRepository.getClubConfig(entityId);
                return clubConfig.getDigitalTicketMode();
            }
        }
    }

}
