package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypeCreateRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerType;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypeUpdateRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerTypesRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public CustomerTypesRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public CustomerTypes getCustomerTypes(Long entityId) {
        return msEntityDatasource.getCustomerTypes(entityId);
    }

    public CustomerType getCustomerType(Long entityId, Long customerTypeId) {
        return msEntityDatasource.getCustomerType(entityId, customerTypeId);
    }

    public IdNameDTO createCustomerTypes(Long entityId, CustomerTypeCreateRequest request) {
        return msEntityDatasource.createCustomerTypes(entityId, request);
    }

    public void updateCustomerType(Long entityId, Long customerTypeId, CustomerTypeUpdateRequest request) {
        msEntityDatasource.updateCustomerTypes(entityId, customerTypeId, request);
    }

    public void deleteCustomerType(Long entityId, Long customerTypeId) {
        msEntityDatasource.deleteCustomerTypes(entityId, customerTypeId);
    }

}
