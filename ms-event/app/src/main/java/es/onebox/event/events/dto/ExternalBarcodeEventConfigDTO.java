package es.onebox.event.events.dto;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serializable;
import java.util.Map;

@CouchDocument
public class ExternalBarcodeEventConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer eventId;
    private Integer entityId;
    private Boolean allow;
    private Map<String, String> dataConfig;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Boolean getAllow() {
        return allow;
    }

    public void setAllow(Boolean allow) {
        this.allow = allow;
    }

    public Map<String, String> getDataConfig() {
        return dataConfig;
    }

    public void setDataConfig(Map<String, String> dataConfig) {
        this.dataConfig = dataConfig;
    }
}
