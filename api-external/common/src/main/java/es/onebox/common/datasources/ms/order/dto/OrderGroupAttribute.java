package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class OrderGroupAttribute implements Serializable {

    @Serial
    private static final long serialVersionUID = 7055401537248512197L;

    private Integer orderGroupAttributeId;
    private Integer orderGroupId;
    private Integer attributeId;
    private Integer valueId;
    private String value;
    private AttributeInfo attributeInfoDTO;

    public OrderGroupAttribute() {
    }

    public Integer getOrderGroupAttributeId() {
        return this.orderGroupAttributeId;
    }

    public void setOrderGroupAttributeId(Integer orderGroupAttributeId) {
        this.orderGroupAttributeId = orderGroupAttributeId;
    }

    public Integer getOrderGroupId() {
        return this.orderGroupId;
    }

    public void setOrderGroupId(Integer orderGroupId) {
        this.orderGroupId = orderGroupId;
    }

    public Integer getAttributeId() {
        return this.attributeId;
    }

    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }

    public Integer getValueId() {
        return this.valueId;
    }

    public void setValueId(Integer valueId) {
        this.valueId = valueId;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AttributeInfo getAttributeInfoDTO() {
        return this.attributeInfoDTO;
    }

    public void setAttributeInfoDTO(AttributeInfo attributeInfoDTO) {
        this.attributeInfoDTO = attributeInfoDTO;
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
