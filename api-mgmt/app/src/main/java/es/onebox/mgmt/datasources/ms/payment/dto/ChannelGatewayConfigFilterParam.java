package es.onebox.mgmt.datasources.ms.payment.dto;

import java.io.Serializable;
import java.util.List;

public class ChannelGatewayConfigFilterParam implements Serializable {

    private static final long serialVersionUID = -3460889871376399317L;

    private String description;
    private List<Key> gatewaysIds;
    private Key gatewayDefault;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Key> getGatewaysIds() {
        return gatewaysIds;
    }

    public void setGatewaysIds(List<Key> gatewaysIds) {
        this.gatewaysIds = gatewaysIds;
    }

    public Key getGatewayDefault() {
        return gatewayDefault;
    }

    public void setGatewayDefault(Key gatewayDefault) {
        this.gatewayDefault = gatewayDefault;
    }
}
