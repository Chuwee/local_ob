package es.onebox.external.dto;

import java.io.Serial;
import java.io.Serializable;

public class ExternalSyncResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ExternalSyncResultStatus status;
    private String message;

    public ExternalSyncResponse(ExternalSyncResultStatus status) {
        this.status = status;
    }

    public ExternalSyncResponse(ExternalSyncResultStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ExternalSyncResultStatus getStatus() {
        return status;
    }

    public void setStatus(ExternalSyncResultStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
