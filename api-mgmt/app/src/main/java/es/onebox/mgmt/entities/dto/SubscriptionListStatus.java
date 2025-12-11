package es.onebox.mgmt.entities.dto;

public enum SubscriptionListStatus {
    ACTIVE(true),
    INACTIVE(false);

    private Boolean isActive;

    SubscriptionListStatus(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getActive() {
        return isActive;
    }

}
