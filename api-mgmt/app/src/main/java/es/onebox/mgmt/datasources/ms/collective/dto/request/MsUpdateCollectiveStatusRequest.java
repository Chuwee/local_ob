package es.onebox.mgmt.datasources.ms.collective.dto.request;

import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveStatus;

import java.io.Serializable;

public class MsUpdateCollectiveStatusRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private CollectiveStatus status;

    public CollectiveStatus getStatus() {
        return status;
    }

    public void setStatus(CollectiveStatus status) {
        this.status = status;
    }
}
