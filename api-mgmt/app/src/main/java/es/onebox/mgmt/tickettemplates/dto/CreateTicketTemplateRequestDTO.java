package es.onebox.mgmt.tickettemplates.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CreateTicketTemplateRequestDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    @NotBlank(message = "name is mandatory")
    private String name;
    @Min(value = 1, message = "entity_id must be above 0")
    @NotNull(message = "entity_id is mandatory")
    @JsonProperty("entity_id")
    private Long entityId;
    @JsonProperty("design_id")
    private Long designId;

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

    public Long getDesignId() {
        return designId;
    }

    public void setDesignId(Long designId) {
        this.designId = designId;
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
