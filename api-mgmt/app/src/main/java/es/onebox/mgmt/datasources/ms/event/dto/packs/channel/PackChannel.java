package es.onebox.mgmt.datasources.ms.event.dto.packs.channel;

import java.io.Serializable;

public class PackChannel implements Serializable {

    private static final long serialVersionUID = 4648843396650159724L;

    private Long id;
    private PackInfo pack;
    private PackChannelInfo channel;
    private PackChannelStatusInfo status;
    private PackChannelSettings settings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PackChannelInfo getChannel() {
        return channel;
    }

    public void setChannel(PackChannelInfo channel) {
        this.channel = channel;
    }

    public PackInfo getPack() {
        return pack;
    }

    public void setPack(PackInfo pack) {
        this.pack = pack;
    }

    public PackChannelStatusInfo getStatus() {
        return status;
    }

    public void setStatus(PackChannelStatusInfo status) {
        this.status = status;
    }

    public PackChannelSettings getSettings() {
        return settings;
    }

    public void setSettings(PackChannelSettings settings) {
        this.settings = settings;
    }
}
