package es.onebox.event.catalog.dto;

import es.onebox.event.catalog.elasticsearch.dto.session.SessionCommunicationElement;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionRelated;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SessionPackSettingsDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = 2934035814661580309L;

    private Long numberOfDays;
    private Long numberOfSessions;
    private List<SessionRelatedDTO> sessions;

    public SessionPackSettingsDTO() {
    }

    public SessionPackSettingsDTO(Long numberOfDays, Long numberOfSessions, List<SessionRelatedDTO> sessions) {
        this.numberOfDays = numberOfDays;
        this.numberOfSessions = numberOfSessions;
        this.sessions = sessions;
    }

    public Long getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(Long numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public Long getNumberOfSessions() {
        return numberOfSessions;
    }

    public void setNumberOfSessions(Long numberOfSessions) {
        this.numberOfSessions = numberOfSessions;
    }

    public List<SessionRelatedDTO> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionRelatedDTO> sessions) {
        this.sessions = sessions;
    }

}
