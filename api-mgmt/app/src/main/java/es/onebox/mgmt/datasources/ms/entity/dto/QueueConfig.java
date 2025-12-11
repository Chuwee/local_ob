package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;

public class QueueConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -5147116444353141159L;

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
