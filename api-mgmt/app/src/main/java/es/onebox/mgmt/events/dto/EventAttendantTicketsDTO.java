package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.AttendantTicketsEventStatusDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EventAttendantTicketsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6436108684186875730L;

    private AttendantTicketsEventStatusDTO status;
    @JsonProperty("channels_scope")
    private AttendantTicketsChannelsScopeDTO channelsScope;
    private Boolean autofill;
    @JsonProperty("edit_attendant")
    private Boolean editAttendant;
    @JsonProperty("edit_autofill")
    private Boolean editAutofill;
    @JsonProperty("edit_autofill_disallowed_sectors")
    private List<Long> editAutofillDisallowedSectors;

    public AttendantTicketsEventStatusDTO getStatus() {
        return status;
    }

    public void setStatus(AttendantTicketsEventStatusDTO status) {
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

    public Boolean getEditAttendant() {
        return editAttendant;
    }

    public void setEditAttendant(Boolean editAttendant) {
        this.editAttendant = editAttendant;
    }

    public Boolean getEditAutofill() {
        return editAutofill;
    }

    public void setEditAutofill(Boolean editAutofill) {
        this.editAutofill = editAutofill;
    }

    public List<Long> getEditAutofillDisallowedSectors() {
        return editAutofillDisallowedSectors;
    }

    public void setEditAutofillDisallowedSectors(List<Long> editAutofillDisallowedSectors) {
        this.editAutofillDisallowedSectors = editAutofillDisallowedSectors;
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
