package es.onebox.mgmt.datasources.ms.entity.dto;

public class EntityVisibility extends EntityOperatorVisibility {

    private Long operatorId;

    public EntityVisibility() {
    }

    public Long getOperatorId() { return operatorId; }

    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
}
