package es.onebox.ath.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TransferMatchDTO implements Serializable {

    private static final long serialVersionUID = 7922489243317370672L;

    @JsonProperty("season_day_number")
    private String seasonDayNumber;
    @JsonProperty("match_date")
    private String matchDate;
    private String season;
    private String match;
    @JsonProperty("competition_id")
    private String competitionId;
    private String rival;
    @JsonProperty("season_day")
    private String seasonDay;

    public String getSeasonDayNumber() {
        return seasonDayNumber;
    }

    public void setSeasonDayNumber(String seasonDayNumber) {
        this.seasonDayNumber = seasonDayNumber;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(String competitionId) {
        this.competitionId = competitionId;
    }

    public String getRival() {
        return rival;
    }

    public void setRival(String rival) {
        this.rival = rival;
    }

    public String getSeasonDay() {
        return seasonDay;
    }

    public void setSeasonDay(String seasonDay) {
        this.seasonDay = seasonDay;
    }
}
