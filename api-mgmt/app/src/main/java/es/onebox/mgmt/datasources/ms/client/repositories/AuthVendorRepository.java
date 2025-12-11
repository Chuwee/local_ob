package es.onebox.mgmt.datasources.ms.client.repositories;

import es.onebox.mgmt.datasources.ms.client.MsClientDatasource;
import es.onebox.mgmt.datasources.ms.client.dto.AuthVendorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthVendorRepository {

    private final MsClientDatasource msClientDatasource;

    @Autowired
    public AuthVendorRepository(MsClientDatasource msClientDatasource) {
        this.msClientDatasource = msClientDatasource;
    }

    public List<AuthVendorConfig> getAuthVendors() {
        return msClientDatasource.getAuthVendors();
    }

    public AuthVendorConfig getAuthVendors(String authVendor) {
        return msClientDatasource.getAuthVendors(authVendor);
    }

}
