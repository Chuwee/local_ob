package es.onebox.common.datasources.distribution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class PresalesRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 7424295449405885213L;

    @JsonProperty("session_id")
    private Long sessionId;
    private Map<String, String> fields;

    public PresalesRequest() {
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;

    }
}
