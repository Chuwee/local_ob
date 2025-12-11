package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.mgmt.events.enums.CustomerTypeAssignationModeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EventCustomerTypeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    @JsonProperty("customer_type")
    private IdNameCodeDTO customerType;
    private CustomerTypeAssignationModeDTO mode;

    public IdNameCodeDTO getCustomerType() {
        return customerType;
    }

    public void setCustomerType(IdNameCodeDTO customerType) {
        this.customerType = customerType;
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
