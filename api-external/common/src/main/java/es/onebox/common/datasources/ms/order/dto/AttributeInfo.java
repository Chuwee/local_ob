package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AttributeInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 7055401537248512197L;

    private Integer id;
    private Integer entityId;
    private String name;
    private Boolean indexed;
    private AttributeSelectionTypes selectionType;
    private AttributeScopes scope;
    private AttributeValueTypes valueType;
    private Integer translation;
    private String code;
    private List<AttributeValueInfo> attributeValueInfos = new ArrayList();

    public AttributeInfo() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEntityId() {
        return this.entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIndexed() {
        return this.indexed;
    }

    public void setIndexed(Boolean indexed) {
        this.indexed = indexed;
    }

    public AttributeSelectionTypes getSelectionType() {
        return this.selectionType;
    }

    public void setSelectionType(AttributeSelectionTypes selectionType) {
        this.selectionType = selectionType;
    }

    public AttributeScopes getScope() {
        return this.scope;
    }

    public void setScope(AttributeScopes scope) {
        this.scope = scope;
    }

    public Integer getTranslation() {
        return this.translation;
    }

    public void setTranslation(Integer translation) {
        this.translation = translation;
    }

    public AttributeValueTypes getValueType() {
        return this.valueType;
    }

    public void setValueType(AttributeValueTypes valueType) {
        this.valueType = valueType;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<AttributeValueInfo> getAttributeValueInfos() {
        return this.attributeValueInfos;
    }

    public void setAttributeValueInfos(List<AttributeValueInfo> attributeValueInfos) {
        this.attributeValueInfos = attributeValueInfos;
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
