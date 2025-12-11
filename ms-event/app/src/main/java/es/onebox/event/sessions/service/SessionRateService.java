package es.onebox.event.sessions.service;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.SessionRateDao;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionRateService {

    private final SessionRateDao sessionRateDao;
    private final SessionDao sessionDao;
    private final RateDao rateDao;

    @Autowired
    public SessionRateService(SessionRateDao sessionRateDao,
                              SessionDao sessionDao,
                              RateDao rateDao) {
        this.sessionRateDao = sessionRateDao;
        this.sessionDao = sessionDao;
        this.rateDao = rateDao;
    }

    public void createSessionRate(Integer sessionId, Integer rateId) {
        if (sessionDao.getById(sessionId) == null) {
            throw OneboxRestException.builder(CoreErrorCode.NOT_FOUND).setMessage("Session not found for id " + sessionId).build();
        }

        if (rateDao.getById(rateId) == null) {
            throw OneboxRestException.builder(CoreErrorCode.NOT_FOUND).setMessage("Rate not found for id " + rateId).build();
        }

        sessionRateDao.createSessionRateRelationship(sessionId, rateId);
    }

    public void deleteSessionRate(Integer sessionId, Integer rateId) {
        try {
            sessionRateDao.deleteRateForSessionId(sessionId, rateId);
        } catch (DataAccessException e) {
            throw OneboxRestException.builder(CoreErrorCode.GENERIC_ERROR).setMessage("Session rate cannot be deleted.").build();
        }
    }
}
