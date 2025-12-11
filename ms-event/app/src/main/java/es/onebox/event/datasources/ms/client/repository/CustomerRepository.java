package es.onebox.event.datasources.ms.client.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.event.datasources.ms.client.MsClientDatasource;
import es.onebox.event.datasources.ms.client.dto.Customer;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class CustomerRepository {

    private final MsClientDatasource datasource;

    public CustomerRepository(MsClientDatasource datasource) {
        this.datasource = datasource;
    }

    @Cached(key = "ms_client_customer", expires = 1, timeUnit = TimeUnit.DAYS)
    public Customer getCustomer(@CachedArg String customerId, @CachedArg Long entityId) {
        return datasource.getCustomer(customerId, entityId);
    }
}
