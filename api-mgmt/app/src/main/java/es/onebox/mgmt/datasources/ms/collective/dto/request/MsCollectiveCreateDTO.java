package es.onebox.mgmt.datasources.ms.collective.dto.request;

import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveType;
import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveValidationMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class MsCollectiveCreateDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotNull
    @Size(min = 1, max = 50)
    private String name;
    @NotNull
    private Long entityId;
    @Size(max = 250)
    private String description;
    @NotNull
    private CollectiveType type;
    @NotNull
    private CollectiveValidationMethod validationMethod;
    private String externalValidator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CollectiveType getType() {
        return type;
    }

    public void setType(CollectiveType type) {
        this.type = type;
    }

    public CollectiveValidationMethod getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(CollectiveValidationMethod validationMethod) {
        this.validationMethod = validationMethod;
    }

    public String getExternalValidator() {
        return externalValidator;
    }

    public void setExternalValidator(String externalValidator) {
        this.externalValidator = externalValidator;
    }
}
