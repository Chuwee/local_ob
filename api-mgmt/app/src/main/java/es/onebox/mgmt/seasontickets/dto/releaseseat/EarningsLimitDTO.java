package es.onebox.mgmt.seasontickets.dto.releaseseat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class EarningsLimitDTO implements Serializable {

    @NotNull
    private Boolean enabled;
    @Min(0)
    @Max(100)
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