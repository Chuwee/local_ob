package es.onebox.mgmt.datasources.ms.event.dto.packs.channel;

import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

public class PackChannelCreate implements Serializable {

    private static final long serialVersionUID = 4648843396650159724L;

    @NotEmpty
    private List<Long> channelIds;

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }
}
