package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventAttendantsConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionAttendantsConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AttendantTicketsRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public AttendantTicketsRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public SessionAttendantsConfigDTO getSessionAttendantsConfig(Long sessionId, Long eventId) {
        return msEventDatasource.getSessionAttendantsConfig(sessionId, eventId);
    }

    public void upsertSessionAttendantsConfig(Long sessionId, Long eventId, SessionAttendantsConfigDTO sessionAttendantsConfig) {
        msEventDatasource.upsertSessionAttendantsConfig(sessionId, eventId, sessionAttendantsConfig);
    }

    public void deleteSessionAttendantsConfig(Long sessionId, Long eventId) {
        msEventDatasource.deleteSessionAttendantsConfig(sessionId, eventId);
    }

    public EventAttendantsConfigDTO getEventAttendantsConfig(Long eventId) {
        return msEventDatasource.getEventAttendantsConfig(eventId);
    }

    public void upsertEventAttendantsConfig(Long eventId, EventAttendantsConfigDTO eventAttendantsConfig) {
        msEventDatasource.upsertEventAttendantsConfig(eventId, eventAttendantsConfig);
    }

    public void deleteEventAttendantsConfig(Long eventId) {
        msEventDatasource.deleteEventAttendantsConfig(eventId);
    }
}
