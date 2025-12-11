package es.onebox.mgmt.customers.converter;

import es.onebox.core.serializer.dto.common.IdCodeDTO;
import es.onebox.mgmt.customers.dto.CustomerConfigDTO;
import es.onebox.mgmt.customers.dto.CustomerRestrictionsDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.customerconfig.CustomerConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.customerconfig.CustomerRestrictions;
import es.onebox.mgmt.datasources.ms.entity.dto.customerconfig.UpdateCustomerConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.customerconfig.UpdateCustomerRestrictions;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class CustomerConfigConverter {

    private CustomerConfigConverter() {
    }

    public static CustomerConfigDTO fromMs(CustomerConfig source) {
        if (source == null) {
            return null;
        }
        CustomerConfigDTO result = new CustomerConfigDTO();
        result.setEntityId(source.getEntityId());
        result.setRestrictions(fromMs(source.getRestrictions()));
        return result;
    }

    public static List<CustomerRestrictionsDTO> fromMs(List<CustomerRestrictions> source) {
        if (CollectionUtils.isEmpty(source)) {
            return null;
        }
        return source.stream().map(CustomerConfigConverter::fromMs).toList();
    }

    public static CustomerRestrictionsDTO fromMs(CustomerRestrictions source) {
        CustomerRestrictionsDTO result = new CustomerRestrictionsDTO();
        result.setKey(source.getKey());
        result.setRestrictedCustomerTypes(source.getRestrictedCustomerTypes().stream().map(IdCodeDTO::getId).toList());

        return result;
    }

    public static UpdateCustomerConfig toMs(CustomerConfigDTO source) {
        if (source == null) {
            return null;
        }
        UpdateCustomerConfig result = new UpdateCustomerConfig();
        result.setEntityId(result.getEntityId());
        result.setRestrictions(toMs(source.getRestrictions()));
        return result;
    }

    public static List<UpdateCustomerRestrictions> toMs(List<CustomerRestrictionsDTO> source) {
        if (source == null) {
            return null;
        }
        return source.stream().map(CustomerConfigConverter::toMs).toList();
    }

    public static UpdateCustomerRestrictions toMs(CustomerRestrictionsDTO source) {
        UpdateCustomerRestrictions result = new UpdateCustomerRestrictions();
        result.setKey(source.getKey());
        result.setRestrictedCustomerTypes(source.getRestrictedCustomerTypes());

        return result;
    }
}
