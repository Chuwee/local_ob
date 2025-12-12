package es.onebox.common.datasources.ms.channel.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class RefundPeriodDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 352679136305044194L;

    private Boolean allowed;
    private Integer timeAfterPurchaseInMinutes;

    public Boolean getAllowed() {
        return allowed;
    }

    public void setAllowed(Boolean allowed) {
        this.allowed = allowed;
    }

    public Integer getTimeAfterPurchaseInMinutes() {
        return timeAfterPurchaseInMinutes;
    }

    public void setTimeAfterPurchaseInMinutes(Integer timeAfterPurchaseInMinutes) {
        this.timeAfterPurchaseInMinutes = timeAfterPurchaseInMinutes;
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
