package es.onebox.fifaqatar.adapter;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.client.dto.request.AuthOrigin;
import es.onebox.common.datasources.ms.client.dto.request.CreateCustomerRequest;
import es.onebox.common.datasources.ms.client.dto.request.CreateCustomerResponse;
import es.onebox.common.datasources.ms.client.dto.request.SearchCustomersRequest;
import es.onebox.common.datasources.ms.client.dto.response.CustomerResponse;
import es.onebox.common.datasources.ms.client.dto.response.CustomersResponse;
import es.onebox.common.datasources.ms.client.repository.CustomerRepository;
import es.onebox.fifaqatar.adapter.datasource.dto.MeResponseDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class FifaQatarCustomerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FifaQatarCustomerHelper.class);

    private final CustomerRepository customerRepository;
    private final FifaQatarMappingHelper fifaQatarMappingHelper;

    public FifaQatarCustomerHelper(CustomerRepository customerRepository, FifaQatarMappingHelper fifaQatarMappingHelper) {
        this.customerRepository = customerRepository;
        this.fifaQatarMappingHelper = fifaQatarMappingHelper;
    }

    @Cached(key = "get_customer", expires = 30, timeUnit = TimeUnit.SECONDS)
    public Customer getCustomer(@CachedArg String customerId) {
        return this.customerRepository.getCustomer(customerId);
    }

    public CustomersResponse getCustomerByEmail(String email, Integer entityId) {
        var searchCustomerReq = new SearchCustomersRequest();
        searchCustomerReq.setEntityId(entityId);
        searchCustomerReq.setEmail(email);
        searchCustomerReq.setLimit(1);

        return customerRepository.searchCustomers(searchCustomerReq);
    }

    public CustomersResponse getCustomerByOriginId(String userId, Integer entityId) {
        var searchCustomerReq = new SearchCustomersRequest();
        searchCustomerReq.setEntityId(entityId);
        searchCustomerReq.setOriginId(userId);
        searchCustomerReq.setLimit(1);

        return customerRepository.searchCustomers(searchCustomerReq);
    }

    public Customer assignCustomerId(MeResponseDTO me, Integer entityId) {
        CustomersResponse customersResponse = getCustomerByOriginId(String.valueOf(me.getId()), entityId);
        if (customersResponse.getData().size() == 1) {
            return customerRepository.getCustomer(customersResponse.getData().get(0).getId());
        }

        CustomersResponse customerByEmail = getCustomerByEmail(me.getEmail(), entityId);
        if (customerByEmail.getData().size() == 1) {
            CustomerResponse customerResponse = customerByEmail.getData().get(0);
            Customer customerToUpdate = customerRepository.getCustomer(customerResponse.getId());
            LOGGER.info("[QATAR] updating customer {} with {}", customerResponse.getId(), me.getId());
            //TODO refactor me please :(
            AuthOrigin feverAuthOrigin = buildAuthOrigin(me);
            List<AuthOrigin> authOrigins = customerToUpdate.getAuthOrigins();
            if (CollectionUtils.isNotEmpty(authOrigins)) {
                List<AuthOrigin> collect = authOrigins.stream().map(origin -> {
                    if ("FEVER".equals(origin.getId())) {
                        return feverAuthOrigin;
                    } else {
                        return origin;
                    }
                }).collect(Collectors.toList());
                customerRepository.updateCustomerAuthOrigins(customerResponse.getId(), entityId, collect);
            } else {
                customerRepository.updateCustomerAuthOrigins(customerResponse.getId(), entityId, List.of(feverAuthOrigin));
            }

            return customerRepository.getCustomer(customerResponse.getId());
        } else {
            String customerIdByOriginId = fifaQatarMappingHelper.getCustomerIdByOriginId(me.getId());
            if (customerIdByOriginId != null) {
                LOGGER.info("[QATAR] customer already created {} {}", me.getId(), me.getEmail());
                return customerRepository.getCustomer(customerIdByOriginId);
            }
            var request = new CreateCustomerRequest();
            request.setEntityId(entityId);
            request.setEmail(me.getEmail());
            request.setName(me.getName());
            request.setSurname(me.getLastName());
            request.setStatus("ACTIVE");
            request.setType("MEMBER");
            request.setAuthOrigins(List.of(buildAuthOrigin(me)));

            //Adding fv id - customer id mapping to prevent conflicts and ES delay
            LOGGER.info("[QATAR] creating customer {} {}", me.getId(), me.getEmail());
            CreateCustomerResponse createdCustomer = customerRepository.createCustomer(request);
            fifaQatarMappingHelper.createCustomerMapping(me.getId(), createdCustomer.getId());

            return customerRepository.getCustomer(createdCustomer.getId());
        }
    }

    private AuthOrigin buildAuthOrigin(MeResponseDTO me) {
        AuthOrigin authOrigin = new AuthOrigin();
        authOrigin.setId(String.valueOf(me.getId()));
        authOrigin.setOrigin("VENDOR");
        authOrigin.setId("FEVER");
        Map<String, String> attrs = new HashMap<>();
        attrs.put("id", String.valueOf(me.getId()));
        authOrigin.setAttributes(attrs);

        return authOrigin;
    }

}
