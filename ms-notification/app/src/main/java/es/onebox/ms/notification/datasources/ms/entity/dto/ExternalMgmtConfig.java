package es.onebox.ms.notification.datasources.ms.entity.dto;

import java.io.Serializable;

public class ExternalMgmtConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long entityId;
    private String endpointUrl;
    private Integer endpointType;
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(Integer endpointType) {
        this.endpointType = endpointType;
    }
}
