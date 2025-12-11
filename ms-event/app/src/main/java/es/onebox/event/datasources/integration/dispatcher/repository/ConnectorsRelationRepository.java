package es.onebox.event.datasources.integration.dispatcher.repository;

import es.onebox.event.datasources.integration.dispatcher.IntDispatcherServiceDatasource;
import es.onebox.event.datasources.integration.dispatcher.dto.ConnectorRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ConnectorsRelationRepository {

    private final IntDispatcherServiceDatasource intDispatcherServiceDatasource;

    @Autowired
    public ConnectorsRelationRepository(IntDispatcherServiceDatasource intDispatcherServiceDatasource) {
        this.intDispatcherServiceDatasource = intDispatcherServiceDatasource;
    }

    public void createConnectorsRelation(ConnectorRelation connectorRelation) {
        this.intDispatcherServiceDatasource.createConnectorsRelation(connectorRelation);
    }
}
