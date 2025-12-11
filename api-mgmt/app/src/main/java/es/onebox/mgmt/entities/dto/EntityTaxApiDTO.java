package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class EntityTaxApiDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String description;
    private Double value;
    @JsonProperty("default")
    private boolean defaultTax = false;

    public EntityTaxApiDTO() {
    }

    public EntityTaxApiDTO(Integer id, String name, String description, Double value, Boolean isDefaultTax) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = value;
        this.defaultTax = isDefaultTax;
    }

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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public boolean isDefaultTax() {
        return defaultTax;
    }

    public void setDefaultTax(Boolean defaultTax) {
        if (defaultTax != null) {
            this.defaultTax = defaultTax;
        }
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
