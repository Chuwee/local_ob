package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class EntitySecurityConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2878327218058235095L;

    @JsonProperty("entity_id")
    private Long entityId;
    @JsonProperty("password_config")
    private PasswordConfigDTO passwordConfig;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public PasswordConfigDTO getPasswordConfig() {
        return passwordConfig;
    }

    public void setPasswordConfig(PasswordConfigDTO passwordConfig) {
        this.passwordConfig = passwordConfig;
    }
}
