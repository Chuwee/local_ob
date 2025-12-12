package es.onebox.flc.orders.dto.groups;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AttributeValuesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3696279462639128613L;

    private AttributeInfoDTO attributeInfo;

    private List<AttributeValueDTO> attributeValue;

    private Integer idAttribute;

    private String description;

    public AttributeInfoDTO getAttributeInfo() {
        return attributeInfo;
    }

    public void setAttributeInfo(AttributeInfoDTO attributeInfo) {
        this.attributeInfo = attributeInfo;
    }

    public List<AttributeValueDTO> getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(List<AttributeValueDTO> attributeValue) {
        this.attributeValue = attributeValue;
    }

    public Integer getIdAttribute() {
        return idAttribute;
    }

    public void setIdAttribute(Integer idAttribute) {
        this.idAttribute = idAttribute;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
