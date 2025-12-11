package es.onebox.mgmt.packs.dto.channels;

import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.PackChannelStatus;

import java.io.Serializable;

public class PackChannelStatusInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private PackChannelStatus request;

    public PackChannelStatus getRequest() {
        return request;
    }

    public void setRequest(PackChannelStatus request) {
        this.request = request;
    }


}
