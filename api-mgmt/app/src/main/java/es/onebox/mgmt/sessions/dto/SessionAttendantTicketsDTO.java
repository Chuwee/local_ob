package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.dto.AttendantTicketsChannelsScopeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SessionAttendantTicketsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5137683093438186778L;

    private AttendantTicketsSessionStatusDTO status;
    @JsonProperty("channels_scope")
    private AttendantTicketsChannelsScopeDTO channelsScope;
    private Boolean autofill;
    @JsonProperty("edit_autofill")
    private Boolean editAutofill;

    public AttendantTicketsSessionStatusDTO getStatus() {
        return status;
    }

    public void setStatus(AttendantTicketsSessionStatusDTO status) {
        this.status = status;
    }

    public AttendantTicketsChannelsScopeDTO getChannelsScope() {
        return channelsScope;
    }

    public void setChannelsScope(AttendantTicketsChannelsScopeDTO channelsScope) {
        this.channelsScope = channelsScope;
    }

    public Boolean getAutofill() {
        return autofill;
    }

    public void setAutofill(Boolean autofill) {
        this.autofill = autofill;
    }

    public Boolean getEditAutofill() {
        return editAutofill;
    }

    public void setEditAutofill(Boolean editAutofill) {
        this.editAutofill = editAutofill;
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
