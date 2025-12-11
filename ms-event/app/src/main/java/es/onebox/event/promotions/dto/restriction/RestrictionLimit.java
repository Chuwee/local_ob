package es.onebox.event.promotions.dto.restriction;

import java.io.Serial;
import java.io.Serializable;

public class RestrictionLimit implements Serializable {

    @Serial
    private static final long serialVersionUID = 7734434131065738093L;

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
