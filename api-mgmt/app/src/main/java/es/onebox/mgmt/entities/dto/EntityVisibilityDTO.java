package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EntityVisibilityDTO extends EntityOperatorVisibilityDTO {

    @JsonProperty("operator_id")
    private Long operatorId;

    public EntityVisibilityDTO() {
    }

    public Long getOperatorId() { return operatorId; }

    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
}
