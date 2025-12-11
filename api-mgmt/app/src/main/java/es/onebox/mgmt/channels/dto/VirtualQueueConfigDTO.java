package es.onebox.mgmt.channels.dto;

import java.io.Serial;
import java.io.Serializable;

public class VirtualQueueConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4258059665847183916L;

    private boolean active;
    private String alias;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
