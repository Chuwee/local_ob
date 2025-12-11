package es.onebox.mgmt.packs.dto.channels;

import java.io.Serializable;

public class PackChannelDTO implements Serializable {

    private static final long serialVersionUID = 4648843396650159724L;

    private PackInfoDTO pack;
    private PackChannelInfoDTO channel;
    private PackChannelStatusInfoDTO status;

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

    public PackChannelStatusInfoDTO getStatus() {
        return status;
    }

    public void setStatus(PackChannelStatusInfoDTO status) {
        this.status = status;
    }
}
