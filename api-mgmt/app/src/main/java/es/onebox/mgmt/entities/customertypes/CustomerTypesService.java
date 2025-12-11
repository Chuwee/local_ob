package es.onebox.mgmt.entities.customertypes;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerType;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypeCreateRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypeUpdateRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypes;
import es.onebox.mgmt.datasources.ms.entity.repository.CustomerTypesRepository;
import es.onebox.mgmt.entities.customertypes.dto.CreateCustomerTypeDTO;
import es.onebox.mgmt.entities.customertypes.dto.CustomerTypeDTO;
import es.onebox.mgmt.entities.customertypes.dto.CustomerTypesDTO;
import es.onebox.mgmt.entities.customertypes.dto.UpdateCustomerTypeDTO;
import es.onebox.mgmt.security.SecurityManager;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerTypesService {

    @Autowired
    private CustomerTypesRepository customerTypesRepository;

    @Autowired
    private SecurityManager securityManager;

    public CustomerTypesDTO getCustomerTypes(Long entityId) {
        securityManager.checkEntityAccessible(entityId);
        CustomerTypes customerTypes = customerTypesRepository.getCustomerTypes(entityId);
        return CustomerTypeConverter.fromMs(customerTypes.getData());
    }

    public CustomerTypeDTO getCustomerType(Long entityId, Long customerTypeId) {
        securityManager.checkEntityAccessible(entityId);
        CustomerType customerType = customerTypesRepository.getCustomerType(entityId, customerTypeId);
        return CustomerTypeConverter.fromMs(customerType);
    }

    public IdDTO createCustomerType(Long entityId, CreateCustomerTypeDTO request) {
        securityManager.checkEntityAccessible(entityId);
        CustomerTypeCreateRequest customerType = CustomerTypeConverter.toMs(request);
        return customerTypesRepository.createCustomerTypes(entityId, customerType);
    }

    public void updateCustomerType(Long entityId, Long customerTypeId, @Valid UpdateCustomerTypeDTO request) {
        securityManager.checkEntityAccessible(entityId);
        CustomerTypeUpdateRequest customerType = CustomerTypeConverter.toMs(request);
        customerTypesRepository.updateCustomerType(entityId, customerTypeId, customerType);
    }

    public void deleteCustomerType(Long entityId, Long customerTypeId) {
        securityManager.checkEntityAccessible(entityId);
        customerTypesRepository.deleteCustomerType(entityId, customerTypeId);
    }
}
