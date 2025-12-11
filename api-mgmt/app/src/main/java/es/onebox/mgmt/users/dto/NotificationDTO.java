package es.onebox.mgmt.users.dto;

import es.onebox.mgmt.datasources.ms.entity.enums.NotificationCode;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class NotificationDTO implements Serializable {


    private static final long serialVersionUID = 1L;

    @NotNull
    private NotificationCode type;

    @NotNull
    private Boolean enable;

    public NotificationDTO() {
    }

    public NotificationDTO(NotificationCode type, Boolean enable) {
        this.type = type;
        this.enable = enable;
    }

    public NotificationCode getType() {
        return type;
    }

    public void setType(NotificationCode type) {
        this.type = type;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
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
