package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;

public class EntityCustomers implements Serializable {

    @Serial
    private static final long serialVersionUID = 7111296474099679566L;

    private Boolean autoAssignOrders;

    public Boolean getAutoAssignOrders() {
        return autoAssignOrders;
    }

    public void setAutoAssignOrders(Boolean autoAssignOrders) {
        this.autoAssignOrders = autoAssignOrders;
    }
}
