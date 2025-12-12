package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class OrderGroup implements Serializable {

    @Serial
    private static final long serialVersionUID = 7055401537248512197L;

    private Long groupId;
    private String orderCode;
    private String name;
    private Integer numAccompanists;
    private Integer numAttendants;
    private List<OrderGroupAttribute> orderGroupAttributes;

    public OrderGroup() {
    }

    public Long getGroupId() {
        return this.groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumAccompanists() {
        return this.numAccompanists;
    }

    public void setNumAccompanists(Integer numAccompanists) {
        this.numAccompanists = numAccompanists;
    }

    public Integer getNumAttendants() {
        return this.numAttendants;
    }

    public void setNumAttendants(Integer numAttendants) {
        this.numAttendants = numAttendants;
    }

    public List<OrderGroupAttribute> getOrderGroupAttributes() {
        return this.orderGroupAttributes;
    }

    public void setOrderGroupAttributes(List<OrderGroupAttribute> orderGroupAttributes) {
        this.orderGroupAttributes = orderGroupAttributes;
    }

    public String getOrderCode() {
        return this.orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
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