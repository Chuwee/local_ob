package es.onebox.mgmt.datasources.ms.client.repositories;

import es.onebox.mgmt.datasources.ms.client.MsClientDatasource;
import es.onebox.mgmt.datasources.ms.client.dto.AuthVendorEntityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AuthVendorEntityRepository {

    private final MsClientDatasource msClientDatasource;

    @Autowired
    public AuthVendorEntityRepository(MsClientDatasource msClientDatasource) {
        this.msClientDatasource = msClientDatasource;
    }

    public AuthVendorEntityConfig getAuthVendorEntityConfiguration(Long entityId) {
        return msClientDatasource.getAuthVendorEntityConfiguration(entityId);
    }

    public void putAuthVendorEntityConfiguration(Long entityId, AuthVendorEntityConfig authVendorEntityConfig) {
        msClientDatasource.putAuthVendorEntityConfiguration(entityId, authVendorEntityConfig);
    }

}
