package es.onebox.mgmt.events.tags.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelsSessionTagsRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotNull(message = "allChannels cannot be null")
    @JsonProperty("all_channels")
    private Boolean allChannels;
    @JsonProperty("selected_channels")
    private List<Long> selectedChannels;

    public Boolean getAllChannels() {
        return allChannels;
    }

    public void setAllChannels(Boolean allChannels) {
        this.allChannels = allChannels;
    }

    public List<Long> getSelectedChannels() {
        return selectedChannels;
    }

    public void setSelectedChannels(List<Long> selectedChannels) {
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
