package es.onebox.common.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EntityEmailNotifications implements Serializable {

    @Serial
    private static final long serialVersionUID = -8756135854609389607L;
    private Long sendLimit;

    public Long getSendLimit() {
        return sendLimit;
    }

    public void setSendLimit(Long sendLimit) {
        this.sendLimit = sendLimit;
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
