package es.onebox.common.datasources.ms.client.repository;

import es.onebox.common.datasources.ms.client.MsClientDatasource;
import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.client.dto.CustomerTypeAutomaticAssignment;
import es.onebox.common.datasources.ms.client.dto.CustomerTypeProcessorResponse;
import es.onebox.common.datasources.ms.client.dto.request.AuthOrigin;
import es.onebox.common.datasources.ms.client.dto.request.CreateCustomerRequest;
import es.onebox.common.datasources.ms.client.dto.request.CreateCustomerResponse;
import es.onebox.common.datasources.ms.client.dto.request.CreateExternalCustomersRequest;
import es.onebox.common.datasources.ms.client.dto.request.SearchCustomersRequest;
import es.onebox.common.datasources.ms.client.dto.response.CustomerResponse;
import es.onebox.common.datasources.ms.client.dto.response.CustomersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerRepository {

    private final MsClientDatasource msClientDatasource;

    @Autowired
    public CustomerRepository(MsClientDatasource datasource) {
        this.msClientDatasource = datasource;
    }

    public Customer getCustomer(String customerId) {
        return msClientDatasource.getCustomer(customerId);
    }

    public CustomersResponse searchCustomers(SearchCustomersRequest request) {
        return msClientDatasource.searchCustomers(request);
    }

    public List<CustomerResponse> getAllCustomers(SearchCustomersRequest request) {
        List<CustomerResponse> customers = new ArrayList<>();
        request.setLimit(1000);
        long offset = 0L;
        while (true) {
            request.setOffset(Math.toIntExact(offset));
            CustomersResponse result = msClientDatasource.searchCustomers(request);
            if (result == null || result.getData() == null || result.getData().isEmpty()) {
                break;
            }
            customers.addAll(result.getData());
            Long next = (result.getMetadata() == null) ? null : result.getMetadata().nextOffset();
            if (next == null || next.equals(offset)) {
                break;
            }
            offset = next;
        }
        return customers;
    }

    public CreateCustomerResponse createCustomer(CreateCustomerRequest request) {
        return msClientDatasource.createCustomer(request);
    }

    public void updateCustomer(String customerId, Integer entityId, Customer request) {
        msClientDatasource.updateCustomer(customerId, entityId, request);
    }

    public void updateCustomerAuthOrigins(String customerId, Integer entityId, List<AuthOrigin> request) {
        msClientDatasource.updateCustomerAuthOrigins(customerId, entityId, request);
    }

    public void updateCustomerUser(String customerId, Integer entityId, Customer request) {
        msClientDatasource.updateCustomerUser(customerId, entityId, request);
    }

    public CustomerTypeProcessorResponse executeCustomerTypeAssignment(String customerId, CustomerTypeAutomaticAssignment body) {
        return msClientDatasource.executeCustomertTypeAutomaticAssignment(customerId, body);
    }

    public void createCustomers(String vendorId, CreateExternalCustomersRequest customersRequest) {
        msClientDatasource.createCustomers(vendorId, customersRequest);
    }

    public void synchronizeExternalCustomers(String vendorId, String customerId) {
        msClientDatasource.synchronizeExternalCustomers(vendorId, customerId);
    }
}
