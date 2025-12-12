package es.onebox.external.service;

import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.common.datasources.ms.client.dto.request.CreateExternalCustomersRequest;
import es.onebox.common.datasources.ms.client.dto.request.SearchCustomersRequest;
import es.onebox.common.datasources.ms.client.dto.response.AuthOrigin;
import es.onebox.common.datasources.ms.client.dto.response.CustomerResponse;
import es.onebox.common.datasources.ms.client.dto.response.CustomersResponse;
import es.onebox.common.datasources.ms.client.repository.CustomerRepository;
import es.onebox.common.datasources.ms.entity.dto.AuthConfigDTO;
import es.onebox.common.datasources.ms.entity.dto.AuthenticatorDTO;
import es.onebox.common.datasources.ms.entity.enums.AuthenticatorTypeDTO;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.external.dto.ExternalSyncRequest;
import es.onebox.external.dto.ExternalSyncResponse;
import es.onebox.external.dto.ExternalSyncResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomerSyncService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerSyncService.class);

    private final CustomerRepository customerRepository;
    private final EntitiesRepository entitiesRepository;

    public CustomerSyncService(CustomerRepository customerRepository, EntitiesRepository entitiesRepository) {
        this.customerRepository = customerRepository;
        this.entitiesRepository = entitiesRepository;
    }

    public ExternalSyncResponse syncCustomers(ExternalSyncRequest request) {
        try {
            LOGGER.info("[SYNC CUSTOMERS] Starting customer synchronization. Requested IDs: {}", request.getIds().size());

            Long entityId = getEntityId();
            String vendorId = getVendorId(entitiesRepository.getEntityAuthConfig(entityId));

            SearchCustomersRequest searchRequest = new SearchCustomersRequest();
            searchRequest.setExternalCustomerIds(request.getIds());
            searchRequest.setEntityId(entityId.intValue());
            searchRequest.setLimit(100);

            CustomersResponse response = customerRepository.searchCustomers(searchRequest);
            List<CustomerResponse> existingCustomers = response.getData();

            Set<String> existingIds = existingCustomers.stream()
                    .flatMap(customer -> customer.getOrigins().stream())
                    .map(AuthOrigin::getAttributes)
                    .flatMap(attrs -> Stream.of(attrs.get("id"), attrs.get("pan")))
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toSet());

            List<String> nonExistingIds = new ArrayList<>();

            for (String externalId : request.getIds()) {
                if (existingIds.contains(externalId)) {
                    existingCustomers.stream()
                            .filter(customer -> customerHasId(customer, externalId))
                            .findFirst()
                            .ifPresent(customer -> customerRepository.synchronizeExternalCustomers(vendorId, customer.getId()));
                } else {
                    LOGGER.info("[SYNC CUSTOMERS] Customer with external ID {} does not exist, marked for creation", externalId);
                    nonExistingIds.add(externalId);
                }
            }

            if (!nonExistingIds.isEmpty()) {
                LOGGER.info("[SYNC CUSTOMERS] Creating {} new customers", nonExistingIds.size());

                CreateExternalCustomersRequest createRequest = new CreateExternalCustomersRequest();
                createRequest.setEntityId(entityId);
                createRequest.setExternalIds(nonExistingIds);
                customerRepository.createCustomers(vendorId, createRequest);

                LOGGER.info("[SYNC CUSTOMERS] Customers created successfully");
            }
            LOGGER.info("[SYNC CUSTOMERS] Customer synchronization completed. Existing: {}, New: {}",
                    existingCustomers.size(), nonExistingIds.size());
            return new ExternalSyncResponse(ExternalSyncResultStatus.SUCCESS);
        } catch (Exception exception) {
            LOGGER.error("[SYNC CUSTOMERS] ERROR with Customers Sync", exception);
            return new ExternalSyncResponse(ExternalSyncResultStatus.ERROR, exception.getMessage());
        }
    }

    private boolean customerHasId(CustomerResponse customer, String externalId) {
        return customer.getOrigins().stream().anyMatch(origin -> {
            Map<String, Object> attrs = origin.getAttributes();
            Object attrId = attrs.get("id");
            Object attrPan = attrs.get("pan");
            return externalId.equals(attrId) || externalId.equals(attrPan);
        });
    }


    private Long getEntityId() {
        AuthenticationData authData = AuthenticationUtils.getAuthDataOrNull();
        if (authData == null || authData.getEntityId() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ENTITY_NOT_FOUND);
        }
        return AuthenticationUtils.getAuthDataOrNull().getEntityId();
    }

    public static String getVendorId(AuthConfigDTO auth) {
        return auth.getAuthenticationMethods().stream()
                .flatMap(authMethod -> authMethod.getAuthenticators().stream())
                .filter(authenticator -> AuthenticatorTypeDTO.VENDOR.equals(authenticator.getType()))
                .findFirst()
                .map(AuthenticatorDTO::getId)
                .orElseThrow(() -> new OneboxRestException(ApiExternalErrorCode.VENDOR_AUTHENTICATOR_NOT_FOUND));
    }
}
