package es.onebox.mgmt.salerequests.gateways.benefit.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class CheckoutCommunicationElementsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5939859507291643672L;

    private BadgeDTO badge;
    private Map<String, String> description;

    public CheckoutCommunicationElementsDTO() {
    }

    public BadgeDTO getBadge() {
        return badge;
    }

    public void setBadge(BadgeDTO badge) {
        this.badge = badge;
    }

    public java.util.Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(java.util.Map<String, String> description) {
        this.description = description;
    }
}
