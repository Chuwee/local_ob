package es.onebox.mgmt.operators.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CreateOperatorTaxRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("name")
    @NotNull(message = "name cannot be empty or null")
    private String name;
    @JsonProperty("description")
    private String description;

    @JsonProperty("value")
    @Min(value = 0, message = "value must be above 0")
    @Max(value = 100, message = "value must be below 100")
    @NotNull(message = "value cannot be empty or null")
    private Double value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
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
