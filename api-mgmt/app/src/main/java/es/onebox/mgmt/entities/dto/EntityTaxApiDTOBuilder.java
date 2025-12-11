package es.onebox.mgmt.entities.dto;

public class EntityTaxApiDTOBuilder {
    private Integer id;
    private String name;
    private String description;
    private Double value;
    private Boolean defaultTax;

    public EntityTaxApiDTOBuilder setId(Integer id) {
        this.id = id;
        return this;
    }

    public EntityTaxApiDTOBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public EntityTaxApiDTOBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public EntityTaxApiDTOBuilder setValue(Double value) {
        this.value = value;
        return this;
    }

    public EntityTaxApiDTOBuilder setDefaultTax(Boolean defaultTax) {
        this.defaultTax = defaultTax;
        return this;
    }

    public EntityTaxApiDTO createEntityTaxApiDTO() {
        return new EntityTaxApiDTO(id, name, description, value, defaultTax);
    }
}
