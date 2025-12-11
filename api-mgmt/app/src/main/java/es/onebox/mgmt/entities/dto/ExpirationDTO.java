package es.onebox.mgmt.entities.dto;

import es.onebox.mgmt.entities.enums.TimeUnit;

import java.io.Serial;
import java.io.Serializable;

public class ExpirationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -10133362485479071L;

    private Boolean enabled;
    private TimeUnit type;
    private Long amount;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public TimeUnit getType() {
        return type;
    }

    public void setType(TimeUnit type) {
        this.type = type;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
