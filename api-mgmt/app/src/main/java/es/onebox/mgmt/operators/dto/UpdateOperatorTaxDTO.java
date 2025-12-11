package es.onebox.mgmt.operators.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateOperatorTaxDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("id")
    @NotNull(message = "id cannot be empty or null")
    private Integer id;

    @JsonProperty("name")
    @NotNull(message = "name cannot be empty or null")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("default")
    @NotNull(message = "default cannot be empty or null")
    private Boolean defaultTax;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Boolean getDefaultTax() {
        return defaultTax;
    }

    public void setDefaultTax(Boolean defaultTax) {
        this.defaultTax = defaultTax;
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
