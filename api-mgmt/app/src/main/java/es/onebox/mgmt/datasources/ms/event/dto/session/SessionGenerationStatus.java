package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.io.Serializable;

public enum SessionGenerationStatus implements Serializable {
    PENDING,
    IN_PROGRESS,
    ACTIVE,
    ERROR;
}
