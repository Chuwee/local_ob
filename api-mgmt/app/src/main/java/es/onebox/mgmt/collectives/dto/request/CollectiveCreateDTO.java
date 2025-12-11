package es.onebox.mgmt.collectives.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.collectives.dto.Type;
import es.onebox.mgmt.collectives.dto.ValidationMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;


public class CollectiveCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 1, max = 50)
    private String name;
    @JsonProperty("entity_id")
    private Long entityId;
    @Size(max = 250)
    private String description;
    @NotNull
    private Type type;
    @NotNull
    @JsonProperty("validation_method")
    private ValidationMethod validationMethod;
    @JsonProperty("external_validator")
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ValidationMethod getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(ValidationMethod validationMethod) {
        this.validationMethod = validationMethod;
    }

    public String getExternalValidator() {
        return externalValidator;
    }

    public void setExternalValidator(String externalValidator) {
        this.externalValidator = externalValidator;
    }
}
