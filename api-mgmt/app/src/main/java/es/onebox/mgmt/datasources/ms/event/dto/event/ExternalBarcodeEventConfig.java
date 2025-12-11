package es.onebox.mgmt.datasources.ms.event.dto.event;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

public class ExternalBarcodeEventConfig implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
