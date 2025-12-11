package es.onebox.mgmt.events.tours.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CreateTourRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "tour name is mandatory")
    private String name;

    @NotNull(message = "entity_id is mandatory")
    @JsonProperty("entity_id")
    private Long entityId;

    public CreateTourRequestDTO() {
    }

    public CreateTourRequestDTO(String name, Long entityId) {
        this.name = name;
        this.entityId = entityId;
    }

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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
