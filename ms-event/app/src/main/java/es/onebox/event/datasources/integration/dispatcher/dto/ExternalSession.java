package es.onebox.event.datasources.integration.dispatcher.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Map;

public class ExternalSession implements Serializable {

    private String id;
    private String name;
    private String description;
    private ZonedDateTime date;
    private ExternalSessionStatus status;
    private Boolean standalone;
    private Map<String, Object> externalProperties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public ExternalSessionStatus getStatus() {
        return status;
    }

    public void setStatus(ExternalSessionStatus status) {
        this.status = status;
    }

    public Boolean getStandalone() {
        return standalone;
    }

    public void setStandalone(Boolean standalone) {
        this.standalone = standalone;
    }

    public Map<String, Object> getExternalProperties() {
        return externalProperties;
    }

    public void setExternalProperties(Map<String, Object> externalProperties) {
        this.externalProperties = externalProperties;
    }
}
