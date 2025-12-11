package es.onebox.event.seasontickets.dao.couch;

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
