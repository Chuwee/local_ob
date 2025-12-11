package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.datasources.ms.entity.enums.NotificationCode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Notification implements Serializable {


    private static final long serialVersionUID = 1L;

    private NotificationCode type;
    private Boolean enable;

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
