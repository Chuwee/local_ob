package es.onebox.event.catalog.dto.promotion;

import java.io.Serial;
import java.io.Serializable;

public class RestrictionLimitDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2473293584169271483L;

    private Boolean enabled;
    private Integer value;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
