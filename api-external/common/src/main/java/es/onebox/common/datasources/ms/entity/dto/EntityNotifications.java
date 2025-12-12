package es.onebox.common.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EntityNotifications implements Serializable {

    @Serial
    private static final long serialVersionUID = -8142581313774072665L;
    private EntityEmailNotifications email;

    public EntityEmailNotifications getEmail() {
        return email;
    }

    public void setEmail(EntityEmailNotifications email) {
        this.email = email;
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
