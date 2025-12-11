package es.onebox.event.sessions.service;

import es.onebox.event.sessions.dao.ExternalSessionConfigConfigCouchDao;
import es.onebox.event.sessions.domain.ExternalSessionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExternalSessionConfigService {

    private final ExternalSessionConfigConfigCouchDao externalSessionConfigConfigCouchDao;

    @Autowired
    public ExternalSessionConfigService(ExternalSessionConfigConfigCouchDao externalSessionConfigConfigCouchDao) {
        this.externalSessionConfigConfigCouchDao = externalSessionConfigConfigCouchDao;
    }

    public void createExternalSessionConfig(Long sessionId, ExternalSessionConfig externalSessionConfig) {
        externalSessionConfigConfigCouchDao.insert(String.valueOf(sessionId), externalSessionConfig);
    }

    public ExternalSessionConfig getExternalSessionConfig(Long sessionId) {
        return externalSessionConfigConfigCouchDao.get(String.valueOf(sessionId));
    }

    public void updateExternalSessionConfig(Long sessionId, ExternalSessionConfig externalSessionConfig) {
        externalSessionConfigConfigCouchDao.upsert(String.valueOf(sessionId), externalSessionConfig);
    }

    public void deleteExternalSessionConfig(Long sessionId) {
        externalSessionConfigConfigCouchDao.remove(String.valueOf(sessionId));
    }

}
