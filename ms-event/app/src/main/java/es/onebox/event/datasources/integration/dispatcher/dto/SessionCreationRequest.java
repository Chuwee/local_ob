package es.onebox.event.datasources.integration.dispatcher.dto;

import java.io.Serial;
import java.io.Serializable;

public class SessionCreationRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -354922972444616882L;

    private Long sessionId;
    private Long relatedSessionId;
    private Long venueConfigId;



    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getRelatedSessionId() {
        return relatedSessionId;
    }

    public void setRelatedSessionId(Long relatedSessionId) {
        this.relatedSessionId = relatedSessionId;
    }

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }
}
