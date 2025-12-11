package es.onebox.mgmt.entities.dto;

import es.onebox.mgmt.entities.enums.EntityCustomContentsType;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class EntityCustomContentsDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotNull(message = "tag must be required")
    private EntityCustomContentsType tag;
    @NotNull(message = "value must be required")
    private String value;

    public EntityCustomContentsType getTag() {
        return tag;
    }

    public void setTag(EntityCustomContentsType tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
