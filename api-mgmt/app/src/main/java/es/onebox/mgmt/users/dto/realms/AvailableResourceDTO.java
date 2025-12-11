package es.onebox.mgmt.users.dto.realms;

import java.io.Serializable;

public class AvailableResourceDTO implements Serializable {

    private static final long serialVersionUID = 1514688598153597539L;

    private String name;
    private Boolean enabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
