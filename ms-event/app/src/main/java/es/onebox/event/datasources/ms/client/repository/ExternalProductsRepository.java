package es.onebox.event.datasources.ms.client.repository;

import es.onebox.event.datasources.ms.client.MsClientDatasource;
import es.onebox.event.datasources.ms.client.dto.CustomerExternalProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExternalProductsRepository {

    private final MsClientDatasource datasource;

    @Autowired
    public ExternalProductsRepository(MsClientDatasource datasource) {
        this.datasource = datasource;
    }

    public List<CustomerExternalProduct> getExternalProductsFromExternalEvent(Integer entityId, String externalEventId) {
        return datasource.getExternalProductsFromExternalEvent(entityId, externalEventId);
    }
}
