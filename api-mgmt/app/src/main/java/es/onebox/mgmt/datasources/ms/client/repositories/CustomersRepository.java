package es.onebox.mgmt.datasources.ms.client.repositories;

import es.onebox.mgmt.datasources.ms.client.MsClientDatasource;
import es.onebox.mgmt.datasources.ms.client.dto.CustomerSearchFilter;
import es.onebox.mgmt.datasources.ms.client.dto.CustomersSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CustomersRepository {

    private final MsClientDatasource msClientDatasource;

    @Autowired
    public CustomersRepository(MsClientDatasource msClientDatasource) {
        this.msClientDatasource = msClientDatasource;
    }

    public CustomersSearch findCustomers(Long entityId, CustomerSearchFilter customerSearchFilter) {
        return msClientDatasource.findCustomers(entityId, customerSearchFilter);
    }
}
