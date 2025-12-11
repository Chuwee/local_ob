package es.onebox.mgmt.customers.services;

import es.onebox.mgmt.customers.converter.CustomerConfigConverter;
import es.onebox.mgmt.customers.dto.CustomerConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.customerconfig.CustomerConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.customerconfig.UpdateCustomerConfig;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerConfigService {

    private final EntitiesRepository entitiesRepository;
    private SecurityManager securityManager;

    @Autowired
    public CustomerConfigService(EntitiesRepository entitiesRepository, SecurityManager securityManager) {
        this.entitiesRepository = entitiesRepository;
        this.securityManager = securityManager;
    }

    public CustomerConfigDTO getCustomerConfig(Long entityId) {
        securityManager.checkEntityAccessible(entityId);
        CustomerConfig customerConfig = entitiesRepository.getCustomerConfig(entityId);
        return CustomerConfigConverter.fromMs(customerConfig);
    }

    public void updateCustomerConfig(Long entityId, CustomerConfigDTO request) {
        securityManager.checkEntityAccessible(entityId);
        UpdateCustomerConfig customerConfig = CustomerConfigConverter.toMs(request);
        entitiesRepository.updateCustomerConfig(entityId, customerConfig);
    }
}
