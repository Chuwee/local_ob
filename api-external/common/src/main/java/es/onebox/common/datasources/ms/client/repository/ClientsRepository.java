package es.onebox.common.datasources.ms.client.repository;

import es.onebox.common.datasources.ms.client.MsClientDatasource;
import es.onebox.common.datasources.ms.client.dto.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ClientsRepository {

    private final MsClientDatasource msClientDatasource;

    @Autowired
    public ClientsRepository(MsClientDatasource msClientDatasource) {
        this.msClientDatasource = msClientDatasource;
    }

    public Client getClient(Long clientId, Long entityId) {
        return msClientDatasource.getClient(clientId, entityId);
    }

}
