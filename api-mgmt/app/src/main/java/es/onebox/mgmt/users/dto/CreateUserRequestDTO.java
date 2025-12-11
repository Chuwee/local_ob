package es.onebox.mgmt.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;


public class CreateUserRequestDTO extends UserDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty(value = "send_email")
    private Boolean sendEmail;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Boolean getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(Boolean sendEmail) {
        this.sendEmail = sendEmail;
    }
}
