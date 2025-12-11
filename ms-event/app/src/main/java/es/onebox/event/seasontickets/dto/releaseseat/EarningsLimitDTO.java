package es.onebox.event.seasontickets.dto.releaseseat;

import java.io.Serial;
import java.io.Serializable;

public class EarningsLimitDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4900488084349192488L;

    private Boolean enabled;
    private Double percentage;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

}