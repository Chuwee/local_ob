package es.onebox.mgmt.entities.dto;

import java.io.Serial;
import java.io.Serializable;

public class QueueConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3954754840711929827L;

    private Boolean active;
    private String alias;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
