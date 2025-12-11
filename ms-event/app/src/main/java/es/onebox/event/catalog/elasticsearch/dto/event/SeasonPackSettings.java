package es.onebox.event.catalog.elasticsearch.dto.event;

import java.io.Serial;
import java.io.Serializable;

public class SeasonPackSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = -7822316511488239359L;

    private Long sessionId;

    public SeasonPackSettings() {
    }

    public SeasonPackSettings(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
