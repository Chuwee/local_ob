package es.onebox.mgmt.passbook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class CreatePassbookTemplateDTO implements Serializable {

    @JsonProperty("entity_id")
    private Long entityId;
    @JsonProperty("origin_entity_id")
    private Long originEntityId;
    @JsonIgnore
    private Long operatorId;
    private String name;
    @NotNull(message = "template_code_to_copy is mandatory")
    @JsonProperty("template_code_to_copy")
    private String templateCodeToCopy;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplateCodeToCopy() {
        return templateCodeToCopy;
    }

    public void setTemplateCodeToCopy(String templateCodeToCopy) {
        this.templateCodeToCopy = templateCodeToCopy;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getOriginEntityId() {
        return originEntityId;
    }

    public void setOriginEntityId(Long originEntityId) {
        this.originEntityId = originEntityId;
    }
}
