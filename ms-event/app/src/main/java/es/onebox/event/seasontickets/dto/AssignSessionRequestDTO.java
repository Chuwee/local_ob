package es.onebox.event.seasontickets.dto;

import java.io.Serial;
import java.io.Serializable;

public class AssignSessionRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5528859102887816994L;

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
