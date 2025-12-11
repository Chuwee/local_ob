package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serializable;

public class SupportEmail implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private String address;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
