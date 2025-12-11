package es.onebox.event.datasources.ms.ticket.repository;

import es.onebox.event.datasources.ms.ticket.MsTicketDatasource;
import es.onebox.event.datasources.ms.ticket.dto.CloneSessionDTO;
import es.onebox.event.datasources.ms.ticket.dto.SessionDTO;
import es.onebox.event.datasources.ms.ticket.enums.CapacityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class SessionRepository {

    private final MsTicketDatasource msTicketDatasource;

    @Autowired
    public SessionRepository(MsTicketDatasource msTicketDatasource) {
        this.msTicketDatasource = msTicketDatasource;
    }

    public void createSession(Long sessionId, Long eventId, Long entityId) {
        msTicketDatasource.createSession(new SessionDTO(sessionId, eventId, entityId));
    }

    public void cloneSession(Long fromSessionId, Long eventId, Long entityId, boolean isActivity, Long toSessionId) {
        CloneSessionDTO cloneSessionDTO = new CloneSessionDTO();
        cloneSessionDTO.setSessionId(toSessionId);
        cloneSessionDTO.setEventId(eventId);
        cloneSessionDTO.setEntityId(entityId);
        cloneSessionDTO.setActivity(isActivity);
        msTicketDatasource.cloneSession(fromSessionId, cloneSessionDTO);
    }

    public List<Long> getSessionQuotas(Long sessionId, CapacityType type, Boolean asSeasonSession) {
        List<Long> quotas = msTicketDatasource.getSessionQuotas(sessionId, type, asSeasonSession);
        if (quotas == null) {
            return Collections.emptyList();
        }
        return quotas;
    }

    public List<Long> getSessionPriceZones(Long sessionId, List<Long> quotaIds, CapacityType type) {
        List<Long> priceZones = msTicketDatasource.getSessionPriceZones(sessionId, quotaIds, type);
        if (priceZones == null) {
            return Collections.emptyList();
        }
        return priceZones;
    }

}
