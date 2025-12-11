package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.EventType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CreateEventRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "event name is mandatory")
    @Size(message = "event name length cannot be above 50 characters", max = 50)
    private String name;

    @Size(message = "event reference length cannot be above 100 characters", max = 100)
    private String reference;

    @NotNull(message = "event type is mandatory")
    @JsonProperty("type")
    private EventType type;

    @NotNull(message = "entity_id is mandatory")
    @Min(value = 1, message = "entity_id must be above 0")
    @JsonProperty("entity_id")
    private Long entityId;

    @NotNull(message = "producer_id is mandatory")
    @Min(value = 1, message = "producer_id must be above 0")
    @JsonProperty("producer_id")
    private Long producerId;

    @NotNull(message = "category_id is mandatory")
    @Min(value = 1, message = "category_id must be above 0")
    @JsonProperty("category_id")
    private Integer categoryId;

    @JsonProperty("currency_code")
    private String currencyCode;

    @JsonProperty("additional_config")
    private AdditionalConfigDTO additionalConfig;

    public CreateEventRequestDTO() {
    }

    public CreateEventRequestDTO(String name, EventType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getProducerId() {
        return producerId;
    }

    public void setProducerId(Long producerId) {
        this.producerId = producerId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCurrencyCode() { return currencyCode; }

    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public AdditionalConfigDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(AdditionalConfigDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
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
