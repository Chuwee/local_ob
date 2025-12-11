package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;

public class UpdateSecurityConfigRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8995317547772173603L;

    private PasswordConfigDTO passwordConfig;

    public PasswordConfigDTO getPasswordConfig() {
        return passwordConfig;
    }

    public void setPasswordConfig(PasswordConfigDTO passwordConfig) {
        this.passwordConfig = passwordConfig;
    }
}
