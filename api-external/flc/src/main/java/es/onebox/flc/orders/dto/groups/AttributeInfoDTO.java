package es.onebox.flc.orders.dto.groups;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AttributeInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3696279462639128613L;

    private Integer attributeId;
    private Integer entityId;
    private String name;
    private AttributeSelectionTypes selectionType;
    private AttributeScopes scope;
    private AttributeValueTypes valueType;
    private String code;

    private List<AttributeValueInfoDTO> attributeValuesInfos;

    public Integer getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttributeSelectionTypes getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(AttributeSelectionTypes selectionType) {
        this.selectionType = selectionType;
    }

    public AttributeScopes getScope() {
        return scope;
    }

    public void setScope(AttributeScopes scope) {
        this.scope = scope;
    }

    public AttributeValueTypes getValueType() {
        return valueType;
    }

    public void setValueType(AttributeValueTypes valueType) {
        this.valueType = valueType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<AttributeValueInfoDTO> getAttributeValuesInfos() {
        return attributeValuesInfos;
    }

    public void setAttributeValuesInfos(List<AttributeValueInfoDTO> attributeValuesInfos) {
        this.attributeValuesInfos = attributeValuesInfos;
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
