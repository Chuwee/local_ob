package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class CreateProducerRequestDTO {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "name is mandatory")
    @Size(max = 200, message = "name must not be greater than 200 characters")
    private String name;

    @NotEmpty(message = "nif is mandatory")
    @Size(max = 30, message = "nif must not be greater than 30 characters")
    private String nif;

    @NotEmpty(message = "social reason is mandatory")
    @Size(max = 100, message = "social reason must not be greater than 100 characters")
    @JsonProperty("social_reason")
    private String socialReason;

    @JsonProperty("entity_id")
    private Long entityId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getSocialReason() {
        return socialReason;
    }

    public void setSocialReason(String socialReason) {
        this.socialReason = socialReason;
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
