package es.onebox.mgmt.datasources.integration.avetsocketconnector.repository;

import es.onebox.mgmt.datasources.integration.avetsocketconnector.MsAvetSocketConnectorDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AvetSocketConnectorRepository {

    private final MsAvetSocketConnectorDatasource msAvetSocketConnectorDatasource;

    @Autowired
    public AvetSocketConnectorRepository (MsAvetSocketConnectorDatasource msAvetSocketConnectorDatasource){
        this.msAvetSocketConnectorDatasource = msAvetSocketConnectorDatasource;
    }

    public void updateMatchAvailability(Long matchId, Long sessionId){
        msAvetSocketConnectorDatasource.updateMatchAvailability(matchId, sessionId);
    }
}
