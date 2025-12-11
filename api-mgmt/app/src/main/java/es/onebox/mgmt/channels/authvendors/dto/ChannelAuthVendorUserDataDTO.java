package es.onebox.mgmt.channels.authvendors.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ChannelAuthVendorUserDataDTO extends ChannelAuthVendorDTO {

    private static final long serialVersionUID = 1L;

    @JsonProperty("mandatory_login")
    private Boolean mandatoryLogin;
    @JsonProperty("editable_data")
    private Boolean editableData;

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
