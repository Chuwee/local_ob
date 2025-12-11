package es.onebox.mgmt.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateUserRequestDTO extends UserDTO {

    private static final long serialVersionUID = 1L;

    @JsonProperty("entity_id")
    private Long entityId;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
