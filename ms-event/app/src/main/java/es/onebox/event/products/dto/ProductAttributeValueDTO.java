package es.onebox.event.products.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductAttributeValueDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    private Long attributeId;
    private Long valueId;
    private String name;
    private Integer position;

    public ProductAttributeValueDTO() {
    }

    public ProductAttributeValueDTO(Long attributeId, Long valueId, String name, Integer position) {
        this.attributeId = attributeId;
        this.valueId = valueId;
        this.name = name;
        this.position = position;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
