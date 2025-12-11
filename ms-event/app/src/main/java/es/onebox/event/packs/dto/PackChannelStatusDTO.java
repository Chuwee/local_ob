package es.onebox.event.packs.dto;

import es.onebox.event.packs.enums.PackChannelStatus;

import java.io.Serializable;

public class PackChannelStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private PackChannelStatus request;

    public PackChannelStatus getRequest() {
        return request;
    }

    public void setRequest(PackChannelStatus request) {
        this.request = request;
    }


}
