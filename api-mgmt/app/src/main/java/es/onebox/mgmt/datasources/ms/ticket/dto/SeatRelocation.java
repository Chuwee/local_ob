package es.onebox.mgmt.datasources.ms.ticket.dto;

import java.io.Serial;
import java.io.Serializable;

public class SeatRelocation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long sourceId;
    private Long destinationId;

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }
}
