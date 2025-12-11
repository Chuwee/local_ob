package es.onebox.event.tickettemplates.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class CloneTicketTemplateDTO implements Serializable {

    private static final long serialVersionUID = 3L;

    @JsonProperty("name")
    @NotBlank(message = "name is mandatory")
    private String name;

    @JsonProperty("entityId")
    private Long entityId;


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
