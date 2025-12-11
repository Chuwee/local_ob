package es.onebox.mgmt.collectives.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class EntitiesAssignationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("entity_admin_id")
    private Long entityAdminId;

    private List<Long> entities;

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public List<Long> getEntities() {
        return entities;
    }

    public void setEntities(List<Long> entities) {
        this.entities = entities;
    }
}
