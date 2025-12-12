package es.onebox.common.datasources.ms.channel.dto;

import es.onebox.common.datasources.ms.channel.enums.DeliveryMethod;
import es.onebox.common.datasources.ms.channel.enums.DeliveryMethodStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

public class ChannelDeliveryMethodDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "type must not be null")
    private DeliveryMethod type;
    @Min(value = 0, message = "cost must be equal or above 0")
    private Double cost;
    private DeliveryMethodStatus status;
    private Boolean defaultMethod;

    public DeliveryMethod getType() {
        return type;
    }

    public void setType(DeliveryMethod type) {
        this.type = type;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public DeliveryMethodStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryMethodStatus status) {
        this.status = status;
    }

    public Boolean getDefaultMethod() {
        return defaultMethod;
    }

    public void setDefaultMethod(Boolean defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
