package es.onebox.mgmt.datasources.ms.event.dto.customertypes;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateEventCustomerType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer customerTypeId;
    private CustomerTypeAssignationMode mode;


    public Integer getCustomerTypeId() {
        return customerTypeId;
    }

    public void setCustomerTypeId(Integer customerTypeId) {
        this.customerTypeId = customerTypeId;
    }

    public CustomerTypeAssignationMode getMode() {
        return mode;
    }

    public void setMode(CustomerTypeAssignationMode mode) {
        this.mode = mode;
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
