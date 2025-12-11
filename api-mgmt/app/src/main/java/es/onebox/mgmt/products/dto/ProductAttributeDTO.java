package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class ProductAttributeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    @JsonProperty("attribute_id")
    private Long attributeId;
    private String name;
    private Integer position;

    public ProductAttributeDTO() {
    }

    public ProductAttributeDTO(Long attributeId, String name, Integer position) {
        this.attributeId = attributeId;
        this.name = name;
        this.position = position;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
