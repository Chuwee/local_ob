package es.onebox.mgmt.entities.dto;

import es.onebox.mgmt.common.BaseEntityRequestFilter;

public class SubscriptionRequestFilterDTO extends BaseEntityRequestFilter {
    private SubscriptionListStatus status;
    private String q;

    public SubscriptionListStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionListStatus status) {
        this.status = status;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }
}
