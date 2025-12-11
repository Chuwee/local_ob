package es.onebox.event.attendants.domain;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public abstract class AttendantsConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -3548505601662650470L;

    private Boolean active;
    private Boolean allChannelsActive;
    private List<Long> activeChannels;
    private Boolean automaticChannelAssignment;
    private Boolean autofill;
    private Boolean allowEditAutofill;
    private List<Long> editAutofillDisallowedSectors;

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Long> getActiveChannels() {
        return activeChannels;
    }

    public void setActiveChannels(List<Long> activeChannels) {
        this.activeChannels = activeChannels;
    }

    public Boolean isAllChannelsActive() {
        return allChannelsActive;
    }

    public void setAllChannelsActive(Boolean allChannelsActive) {
        this.allChannelsActive = allChannelsActive;
    }

    public Boolean isAutomaticChannelAssignment() {
        return automaticChannelAssignment;
    }

    public void setAutomaticChannelAssignment(Boolean automaticChannelAssignment) {
        this.automaticChannelAssignment = automaticChannelAssignment;
    }

    public Boolean isAutofill() {
        return autofill;
    }

    public void setAutofill(Boolean autofill) {
        this.autofill = autofill;
    }

    public Boolean isAllowEditAutofill() {
        return allowEditAutofill;
    }

    public void setAllowEditAutofill(Boolean allowEditAutofill) {
        this.allowEditAutofill = allowEditAutofill;
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
