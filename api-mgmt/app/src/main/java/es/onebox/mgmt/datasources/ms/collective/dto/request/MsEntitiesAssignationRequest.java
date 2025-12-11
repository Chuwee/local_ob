package es.onebox.mgmt.datasources.ms.collective.dto.request;

import java.io.Serializable;
import java.util.List;

public class MsEntitiesAssignationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> entities;
    private Long operatorId;
    private Long entityAdminId;

    public List<Long> getEntities() {
        return entities;
    }

    public void setEntities(List<Long> entities) {
        this.entities = entities;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getEntityAdminId() { return entityAdminId; }

    public void setEntityAdminId(Long entityAdminId) {this.entityAdminId = entityAdminId; }
}
