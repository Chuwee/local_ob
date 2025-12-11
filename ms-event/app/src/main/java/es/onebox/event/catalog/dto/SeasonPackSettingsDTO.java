package es.onebox.event.catalog.dto;

import java.io.Serial;
import java.io.Serializable;

public class SeasonPackSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7396115623675515728L;

    private Long sessionId;

    public SeasonPackSettingsDTO() {
    }

    public SeasonPackSettingsDTO(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
