package es.onebox.mgmt.entities.dto;

import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SettingsNotificationsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Valid
    private SettingsEmailNotificationsDTO email;

    public SettingsEmailNotificationsDTO getEmail() {
        return email;
    }

    public void setEmail(SettingsEmailNotificationsDTO email) {
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
