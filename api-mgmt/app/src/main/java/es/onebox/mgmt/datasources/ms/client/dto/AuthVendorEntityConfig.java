package es.onebox.mgmt.datasources.ms.client.dto;

import java.io.Serializable;
import java.util.List;

public class AuthVendorEntityConfig implements Serializable {

    private Integer entityId;

    private Boolean allowed;

    private List<String> vendors;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer id) {
        this.entityId = id;
    }

    public List<String> getvendors() {
        return vendors;
    }

    public void setVendors(List<String> vendors) {
        this.vendors = vendors;
    }

    public Boolean getAllowed() { return allowed; }

    public void setAllowed(Boolean allowed) { this.allowed = allowed; }

}
