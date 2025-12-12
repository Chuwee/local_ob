package es.onebox.common.datasources.ms.event.dto;

import es.onebox.common.datasources.ms.event.enums.EventTagType;
import java.io.Serializable;
import java.util.Set;

public class EventCommunicationElementFilter implements Serializable {

    private Set<EventTagType> tags;
    private Integer languageId;
    private String language;

    public Set<EventTagType> getTags() {
        return tags;
    }

    public void setTags(Set<EventTagType> tags) {
        this.tags = tags;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}