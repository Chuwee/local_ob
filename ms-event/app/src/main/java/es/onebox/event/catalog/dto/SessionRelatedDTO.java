package es.onebox.event.catalog.dto;

import es.onebox.event.catalog.elasticsearch.dto.session.SessionCommunicationElement;

import javax.xml.catalog.Catalog;
import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public class SessionRelatedDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6049728293824716229L;

    private Long id;
    private String name;
    private SessionRelatedDateDTO date;
    private List<CatalogCommunicationElementDTO> communicationElements;

    public SessionRelatedDTO(){
    }

    public SessionRelatedDTO(Long id, String name, SessionRelatedDateDTO date, List<CatalogCommunicationElementDTO> communicationElements) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.communicationElements = communicationElements;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SessionRelatedDateDTO getDate() {
        return date;
    }

    public void setDate(SessionRelatedDateDTO date) {
        this.date = date;
    }

    public List<CatalogCommunicationElementDTO> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<CatalogCommunicationElementDTO> communicationElements) {
        this.communicationElements = communicationElements;
    }
}
