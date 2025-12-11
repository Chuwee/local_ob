package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.io.Serializable;
import java.util.Map;

public class ExternalBarcodeSessionConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sessionId;
    private Integer eventId;
    private Map<String, String> dataConfig;

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Map<String, String> getDataConfig() {
        return dataConfig;
    }

    public void setDataConfig(Map<String, String> dataConfig) {
        this.dataConfig = dataConfig;
    }
}
