package es.onebox.mgmt.datasources.ms.event.dto.tags;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelsSessionTags implements Serializable {

    private static final long serialVersionUID = 1L;
    private Boolean allChannels;
    private List<ChannelSessionTag> selectedChannels;

    public Boolean getAllChannels() {
        return allChannels;
    }

    public void setAllChannels(Boolean allChannels) {
        this.allChannels = allChannels;
    }

    public List<ChannelSessionTag> getSelectedChannels() {
        return selectedChannels;
    }

    public void setSelectedChannels(List<ChannelSessionTag> selectedChannels) {
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
