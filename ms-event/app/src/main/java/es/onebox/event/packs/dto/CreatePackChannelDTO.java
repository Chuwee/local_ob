package es.onebox.event.packs.dto;

import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

public class CreatePackChannelDTO implements Serializable {

    private static final long serialVersionUID = -5423675427715060709L;

    @NotEmpty
    private List<Long> channelIds;

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }
}
