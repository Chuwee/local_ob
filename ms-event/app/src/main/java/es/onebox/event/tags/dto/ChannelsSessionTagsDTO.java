package es.onebox.event.tags.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelsSessionTagsDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Boolean allChannels;
    private List<ChannelSessionTagDTO> selectedChannels;

    public Boolean getAllChannels() {
        return allChannels;
    }

    public void setAllChannels(Boolean allChannels) {
        this.allChannels = allChannels;
    }

    public List<ChannelSessionTagDTO> getSelectedChannels() {
        return selectedChannels;
    }

    public void setSelectedChannels(List<ChannelSessionTagDTO> selectedChannels) {
        this.selectedChannels = selectedChannels;
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
