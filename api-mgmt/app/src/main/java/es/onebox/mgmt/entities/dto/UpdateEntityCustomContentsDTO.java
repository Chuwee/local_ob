package es.onebox.mgmt.entities.dto;

import es.onebox.mgmt.entities.enums.EntityCustomContentsExtension;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class UpdateEntityCustomContentsDTO extends EntityCustomContentsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "extension must be required")
    private EntityCustomContentsExtension extension;

    public UpdateEntityCustomContentsDTO() {
    }

    public EntityCustomContentsExtension getExtension() {
        return extension;
    }

    public void setExtension(EntityCustomContentsExtension extension) {
        this.extension = extension;
    }

}
