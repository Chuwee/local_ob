package es.onebox.mgmt.datasources.ms.channel.dto.authvendor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelAuthVendorUserData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean allowed;
    private Boolean mandatoryLogin;
    private Boolean editableData;

    public Boolean getAllowed() {
        return this.allowed;
    }

    public void setAllowed(Boolean allowed) {
        this.allowed = allowed;
    }

    public Boolean getMandatoryLogin() {
        return this.mandatoryLogin;
    }

    public void setMandatoryLogin(Boolean mandatoryLogin) {
        this.mandatoryLogin = mandatoryLogin;
    }

    public Boolean getEditableData() {
        return this.editableData;
    }

    public void setEditableData(Boolean editableData) {
        this.editableData = editableData;
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
