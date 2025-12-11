package es.onebox.mgmt.datasources.integration.avetconfig.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class Match implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private Long matchId;
    private ZonedDateTime matchDate;
    private ZonedDateTime startSalesDate;
    private ZonedDateTime endSalesDate;
    private Boolean matchDateConfirmed;
    private Boolean smartBookingRelated;

    public Match() {
    }

    public Match(String name, ZonedDateTime matchDate, ZonedDateTime startSalesDate, ZonedDateTime endSalesDate,
                 Boolean matchDateConfirmed, Boolean smartBookingRelated) {
        this.name = name;
        this.matchDate = matchDate;
        this.startSalesDate = startSalesDate;
        this.endSalesDate = endSalesDate;
        this.matchDateConfirmed = matchDateConfirmed;
        this.smartBookingRelated = smartBookingRelated;
    }

    public String getName() {
        return name;
    }

    public ZonedDateTime getMatchDate() {
        return matchDate;
    }

    public ZonedDateTime getStartSalesDate() {
        return startSalesDate;
    }

    public ZonedDateTime getEndSalesDate() {
        return endSalesDate;
    }

    public Boolean isMatchDateConfirmed() {
        return matchDateConfirmed;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Boolean getSmartBookingRelated() {
        return smartBookingRelated;
    }
}
