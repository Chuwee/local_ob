package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serial;
import java.io.Serializable;

public class VirtualQueueConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -6584277076814715053L;

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
