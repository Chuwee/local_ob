package es.onebox.event.packs.dto;

import java.io.Serializable;

public class PackChannelDTO implements Serializable {

    private static final long serialVersionUID = 4648843396650159724L;

    private Long id;
    private PackInfoDTO pack;
    private PackChannelInfoDTO channel;
    private PackChannelStatusDTO status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PackChannelInfoDTO getChannel() {
        return channel;
    }

    public void setChannel(PackChannelInfoDTO channel) {
        this.channel = channel;
    }

    public PackInfoDTO getPack() {
        return pack;
    }

    public void setPack(PackInfoDTO pack) {
        this.pack = pack;
    }

    public PackChannelStatusDTO getStatus() {
        return status;
    }

    public void setStatus(PackChannelStatusDTO status) {
        this.status = status;
    }
}
