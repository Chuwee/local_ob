package es.onebox.fifaqatar.config.config;

import java.io.Serial;
import java.io.Serializable;

public class DeliverySettings implements Serializable {


    @Serial
    private static final long serialVersionUID = -9173959308976937423L;

    private Boolean enabled;
    private Integer hoursBefore;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getHoursBefore() {
        return hoursBefore;
    }

    public void setHoursBefore(Integer hoursBefore) {
        this.hoursBefore = hoursBefore;
    }
}
