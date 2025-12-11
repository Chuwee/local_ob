package es.onebox.mgmt.datasources.ms.event.dto.packs.channel;

import java.io.Serializable;

public class PackChannelRequestSales implements Serializable {

    private static final long serialVersionUID = -4188552469540900094L;

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
