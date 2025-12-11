package es.onebox.event.catalog.elasticsearch.dto.session;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SessionPackSettings implements Serializable {


    @Serial
    private static final long serialVersionUID = 2934035814661580309L;

    private Long numberOfDays;
    private Long numberOfSessions;
    private List<SessionRelated> sessions;

    public SessionPackSettings() {
    }

    public SessionPackSettings(Long numberOfDays, Long numberOfSessions, List<SessionRelated> sessions) {
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

    public List<SessionRelated> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionRelated> sessions) {
        this.sessions = sessions;
    }
}
