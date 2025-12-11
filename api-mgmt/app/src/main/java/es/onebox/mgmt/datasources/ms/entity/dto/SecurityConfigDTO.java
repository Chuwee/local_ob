package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;

public class SecurityConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1091074660853409367L;

    private Long entityId;
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
