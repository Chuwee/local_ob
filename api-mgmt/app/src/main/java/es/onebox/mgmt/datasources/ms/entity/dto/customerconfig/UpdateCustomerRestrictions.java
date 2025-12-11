package es.onebox.mgmt.datasources.ms.entity.dto.customerconfig;

import es.onebox.mgmt.customers.enums.CustomerFunctionality;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateCustomerRestrictions implements Serializable {

    @Serial
    private static final long serialVersionUID = -7221445391572386890L;

    private CustomerFunctionality key;
    private List<Long> restrictedCustomerTypes;

    public CustomerFunctionality getKey() {
        return key;
    }

    public void setKey(CustomerFunctionality key) {
        this.key = key;
    }

    public List<Long> getRestrictedCustomerTypes() {
        return restrictedCustomerTypes;
    }

    public void setRestrictedCustomerTypes(List<Long> restrictedCustomerTypes) {
        this.restrictedCustomerTypes = restrictedCustomerTypes;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
