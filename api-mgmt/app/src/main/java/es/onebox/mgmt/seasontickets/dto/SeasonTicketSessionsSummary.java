package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SeasonTicketSessionsSummary implements Serializable {

    private static final long serialVersionUID = 7969913521800306362L;

    @JsonProperty("assigned_sessions")
    private Integer assignedSessions;

    @JsonProperty("sessions_on_sale")
    private Integer sessionsOnSale;

    @JsonProperty("listed_events")
    private Integer listedEvents;

    @JsonProperty("total_sessions")
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
