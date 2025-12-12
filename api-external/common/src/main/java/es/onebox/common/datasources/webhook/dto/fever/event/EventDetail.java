package es.onebox.common.datasources.webhook.dto.fever.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.event.enums.EventStatus;

import java.io.Serializable;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public class EventDetail implements Serializable {

    private String name;
    private EventStatus status;
    private List<EventLanguageFeverDTO> languages;
    private Boolean supraEvent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public List<EventLanguageFeverDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<EventLanguageFeverDTO> languages) {
        this.languages = languages;
    }

    public Boolean getSupraEvent() {
        return supraEvent;
    }

    public void setSupraEvent(Boolean supraEvent) {
        this.supraEvent = supraEvent;
    }
}
