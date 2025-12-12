package es.onebox.common.datasources.common.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

public class B2BPrice implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal conditions;

    private BigDecimal commission;

    public BigDecimal getConditions() {
        return conditions;
    }

    public void setConditions(BigDecimal conditions) {
        this.conditions = conditions;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
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
