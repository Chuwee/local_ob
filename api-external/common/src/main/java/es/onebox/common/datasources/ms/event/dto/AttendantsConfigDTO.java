package es.onebox.common.datasources.ms.event.dto;

import java.io.Serializable;
import java.util.List;

public class AttendantsConfigDTO implements Serializable {

    private Boolean active;
    private Boolean allChannelsActive;
    private List<Long> activeChannels;

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean getAllChannelsActive() {
        return allChannelsActive;
    }

    public void setAllChannelsActive(Boolean allChannelsActive) {
        this.allChannelsActive = allChannelsActive;
    }

    public List<Long> getActiveChannels() {
        return activeChannels;
    }

    public void setActiveChannels(List<Long> activeChannels) {
        this.activeChannels = activeChannels;
    }
}
