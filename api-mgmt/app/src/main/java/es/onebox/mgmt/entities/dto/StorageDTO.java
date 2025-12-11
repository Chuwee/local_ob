package es.onebox.mgmt.entities.dto;

import java.io.Serial;
import java.io.Serializable;

public class StorageDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 128328030188830145L;

    private Boolean enabled;
    private Long amount;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
