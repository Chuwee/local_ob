package es.onebox.mgmt.datasources.ms.event.dto.packs.channel;

import java.io.Serializable;

public class PackChannelStatusInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private PackChannelStatus request;

    public PackChannelStatus getRequest() {
        return request;
    }

    public void setRequest(PackChannelStatus request) {
        this.request = request;
    }


}
