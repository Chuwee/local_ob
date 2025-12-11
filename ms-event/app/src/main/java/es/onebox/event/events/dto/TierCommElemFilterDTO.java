package es.onebox.event.events.dto;

import es.onebox.event.events.enums.CommunicationElementType;

import java.io.Serializable;

public class TierCommElemFilterDTO implements Serializable {

    private CommunicationElementType type;
    private String language;

    public CommunicationElementType getType() {
        return type;
    }

    public void setType(CommunicationElementType type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
