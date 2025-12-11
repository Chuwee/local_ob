package es.onebox.mgmt.sessions.enums;

import java.io.Serializable;

public enum SessionGenerationStatus implements Serializable {
    PENDING,
    IN_PROGRESS,
    ACTIVE,
    ERROR;
}
