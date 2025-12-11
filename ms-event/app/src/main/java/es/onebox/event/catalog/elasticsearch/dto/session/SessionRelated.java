package es.onebox.event.catalog.elasticsearch.dto.session;

import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SessionRelated implements Serializable {

    @Serial
    private static final long serialVersionUID = -4818238008039512946L;

    private Long id;
    private String name;
    private SessionRelatedDate date;
    private List<SessionCommunicationElement>  communicationElements;

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

    public SessionRelatedDate getDate() {
        return date;
    }

    public void setDate(SessionRelatedDate date) {
        this.date = date;
    }

    public List<SessionCommunicationElement>  getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<SessionCommunicationElement>  communicationElements) {
        this.communicationElements = communicationElements;
    }
}
