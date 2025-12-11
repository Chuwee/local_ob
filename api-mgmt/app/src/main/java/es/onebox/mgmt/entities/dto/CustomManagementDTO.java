package es.onebox.mgmt.entities.dto;

import es.onebox.mgmt.entities.enums.CustomManagementType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CustomManagementDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8769888604504870475L;

    private Boolean enabled;

    private CustomManagementType type;

    public CustomManagementDTO() {}

    public CustomManagementDTO(Boolean enabled, CustomManagementType type) {
        this.enabled = enabled;
        this.type = type;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public CustomManagementType getType() {
        return type;
    }

    public void setType(CustomManagementType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
