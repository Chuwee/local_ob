package es.onebox.event.loyaltypoints.sessions.service;

import es.onebox.event.loyaltypoints.sessions.converter.SessionLoyaltyPointsConverter;
import es.onebox.event.loyaltypoints.sessions.dao.SessionLoyaltyPointsConfigCouchDao;
import es.onebox.event.loyaltypoints.sessions.domain.SessionLoyaltyPointsConfig;
import es.onebox.event.loyaltypoints.sessions.dto.SessionLoyaltyPointsConfigDTO;
import es.onebox.event.loyaltypoints.sessions.dto.UpdateSessionLoyaltyPointsConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionLoyaltyPointsService {

    private final SessionLoyaltyPointsConfigCouchDao sessionLoyaltyPointsConfigCouchDao;

    @Autowired
    public SessionLoyaltyPointsService(SessionLoyaltyPointsConfigCouchDao sessionLoyaltyPointsConfigCouchDao) {
        this.sessionLoyaltyPointsConfigCouchDao = sessionLoyaltyPointsConfigCouchDao;
    }

    public SessionLoyaltyPointsConfigDTO getSessionLoyaltyPointsConfig(Long sessionId) {
        return SessionLoyaltyPointsConverter.toDTO(sessionLoyaltyPointsConfigCouchDao.get(sessionId.toString()));
    }

    public void updateSessionLoyaltyPointsConfig(Long sessionId, UpdateSessionLoyaltyPointsConfigDTO updateSessionLoyaltyPointsConfigDTO) {
        if (updateSessionLoyaltyPointsConfigDTO == null) {
            return;
        }
        SessionLoyaltyPointsConfig sessionLoyaltyPointsConfig = sessionLoyaltyPointsConfigCouchDao.getOrInitSessionLoyaltyPointsConfig(sessionId);
        SessionLoyaltyPointsConverter.updateLoyaltyPointsConfig(sessionLoyaltyPointsConfig, updateSessionLoyaltyPointsConfigDTO);
        sessionLoyaltyPointsConfigCouchDao.upsert(sessionId.toString(), sessionLoyaltyPointsConfig);
    }
}
