package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.CustomerTypeAssignationModeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateEventCustomerTypeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("customer_type_id")
    private Integer customerTypeId;
    private CustomerTypeAssignationModeDTO mode;


    public Integer getCustomerTypeId() {
        return customerTypeId;
    }

    public void setCustomerTypeId(Integer customerTypeId) {
        this.customerTypeId = customerTypeId;
    }

    public CustomerTypeAssignationModeDTO getMode() {
        return mode;
    }

    public void setMode(CustomerTypeAssignationModeDTO mode) {
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
