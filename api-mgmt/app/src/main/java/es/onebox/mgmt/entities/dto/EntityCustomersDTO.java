package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class EntityCustomersDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7111296474099679566L;

    @JsonProperty("auto_assign_orders")
    private Boolean autoAssignOrders;

    public Boolean getAutoAssignOrders() {
        return autoAssignOrders;
    }

    public void setAutoAssignOrders(Boolean autoAssignOrders) {
        this.autoAssignOrders = autoAssignOrders;
    }
}
