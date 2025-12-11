package es.onebox.mgmt.channels.dto;

import es.onebox.mgmt.channels.enums.CustomerAssignationMode;

import java.io.Serial;
import java.io.Serializable;

public class CustomerAssignationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8758607126563900060L;

    private Boolean enabled;
    private CustomerAssignationMode mode;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public CustomerAssignationMode getMode() {
        return mode;
    }

    public void setMode(CustomerAssignationMode mode) {
        this.mode = mode;
    }
}
