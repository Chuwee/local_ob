package es.onebox.mgmt.datasources.ms.payment.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChannelGatewayConfigFilter implements Serializable {
    private String id;
    private String name;
    private Integer idChannel;
    private String description;
    private List<Key> gatewaysIds = new ArrayList();
    private Key gatewayDefault;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(Integer idChannel) {
        this.idChannel = idChannel;
    }

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
