package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

public class EarningsLimit {

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
