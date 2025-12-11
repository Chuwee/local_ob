package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.datasources.ms.entity.enums.MemberOrderType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class MemberOperationsPeriod implements Serializable {

    @Serial
    private static final long serialVersionUID = 2622973950650097086L;

    private MemberOrderType orderType;

    public MemberOperationsPeriod() {
    }

    public MemberOrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(MemberOrderType orderType) {
        this.orderType = orderType;
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
