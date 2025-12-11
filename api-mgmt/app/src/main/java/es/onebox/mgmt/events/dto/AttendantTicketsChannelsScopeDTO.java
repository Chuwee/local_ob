package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.events.enums.AttendantTicketsChannelScopeTypeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class AttendantTicketsChannelsScopeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private AttendantTicketsChannelScopeTypeDTO type;
    @JsonProperty("channels")
    private List<IdNameDTO> channels;
    @JsonProperty("add_new_event_channel_relationships")
    private Boolean addNewEventChannelRelationships;

    public AttendantTicketsChannelScopeTypeDTO getType() {
        return type;
    }

    public void setType(AttendantTicketsChannelScopeTypeDTO type) {
        this.type = type;
    }

    public List<IdNameDTO> getChannels() {
        return channels;
    }

    public void setChannels(List<IdNameDTO> channels) {
        this.channels = channels;
    }

    public Boolean getAddNewEventChannelRelationships() {
        return addNewEventChannelRelationships;
    }

    public void setAddNewEventChannelRelationships(Boolean addNewEventChannelRelationships) {
        this.addNewEventChannelRelationships = addNewEventChannelRelationships;
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
