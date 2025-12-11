package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;

public class SeasonTicketSessionsSummary implements Serializable {

    private static final long serialVersionUID = 5202903506339614928L;

    private Integer assignedSessions;

    private Integer sessionsOnSale;

    private Integer listedEvents;

    private Integer totalSessions;

    public Integer getAssignedSessions() {
        return assignedSessions;
    }

    public void setAssignedSessions(Integer assignedSessions) {
        this.assignedSessions = assignedSessions;
    }

    public Integer getSessionsOnSale() {
        return sessionsOnSale;
    }

    public void setSessionsOnSale(Integer sessionsOnSale) {
        this.sessionsOnSale = sessionsOnSale;
    }

    public Integer getListedEvents() {
        return listedEvents;
    }

    public void setListedEvents(Integer listedEvents) {
        this.listedEvents = listedEvents;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }
}
