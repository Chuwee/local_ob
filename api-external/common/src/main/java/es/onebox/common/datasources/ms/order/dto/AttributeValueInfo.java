package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AttributeValueInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 7055401537248512197L;

    private Integer id;
    private Integer attributeId;
    private String name;
    private Boolean defaultAttribute;
    private Integer translation;
    private String code;

    public AttributeValueInfo() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAttributeId() {
        return this.attributeId;
    }

    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDefaultAttribute() {
        return this.defaultAttribute;
    }

    public void setDefaultAttribute(Boolean defaultAttribute) {
        this.defaultAttribute = defaultAttribute;
    }

    public Integer getTranslation() {
        return this.translation;
    }

    public void setTranslation(Integer translation) {
        this.translation = translation;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
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
