package es.onebox.event.datasources.ms.client.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.event.datasources.ms.client.MsClientDatasource;
import es.onebox.event.datasources.ms.client.dto.ClientEntity;
import es.onebox.event.datasources.ms.client.dto.conditions.ClientConditionsDTO;
import es.onebox.event.datasources.ms.client.dto.conditions.ConditionsRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class ClientRepository {

    private final MsClientDatasource datasource;

    public ClientRepository(MsClientDatasource datasource) {
        this.datasource = datasource;
    }

    @Cached(key = "ms_client_client_entities", expires = 3, timeUnit = TimeUnit.MINUTES)
    public List<ClientEntity> getCachedClientEntities(@CachedArg Long entityId) {
        return datasource.getClientEntities(entityId);
    }

    public ClientConditionsDTO getClientConditions(ConditionsRequest req) {
        return datasource.getConditions(req);
    }

}
