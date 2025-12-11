package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serial;
import java.io.Serializable;

public class AssignSessionRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -674720716403090074L;

    private Long sessionId;
    private Boolean updateBarcodes;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getUpdateBarcodes() {
        return updateBarcodes;
    }

    public void setUpdateBarcodes(Boolean updateBarcodes) {
        this.updateBarcodes = updateBarcodes;
    }
}