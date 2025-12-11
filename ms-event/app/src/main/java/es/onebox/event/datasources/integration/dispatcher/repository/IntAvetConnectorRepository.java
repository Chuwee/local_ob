package es.onebox.event.datasources.integration.dispatcher.repository;

import es.onebox.event.datasources.integration.dispatcher.IntDispatcherServiceDatasource;
import es.onebox.event.datasources.integration.dispatcher.dto.SessionCreationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class IntAvetConnectorRepository {

    private final IntDispatcherServiceDatasource datasource;

    @Autowired
    public IntAvetConnectorRepository(IntDispatcherServiceDatasource datasource) {
        this.datasource = datasource;
    }

    public void deleteExternalSession(Long entityId, Long eventId, Long sessionId) {
        datasource.deleteExternalSession(entityId, eventId, sessionId);
    }

}
