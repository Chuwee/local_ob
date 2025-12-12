package es.onebox.common.datasources.avetconfig.dto;

import java.io.Serial;
import java.io.Serializable;

public class SessionMatch implements Serializable {

    @Serial
    private static final long serialVersionUID = -7268693228674214680L;

    private Long sessionId;
    private Integer matchId;
    private Integer avetMatchId;
    private String seasonId;
    private String matchName;
    private Integer partnerTicketsLimit;
    private Integer partnerCompanionTicketsLimit;
    private Integer capacityId;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public Integer getAvetMatchId() {
        return avetMatchId;
    }

    public void setAvetMatchId(Integer avetMatchId) {
        this.avetMatchId = avetMatchId;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public Integer getCapacityId() {
        return capacityId;
    }

    public void setCapacityId(Integer capacityId) {
        this.capacityId = capacityId;
    }

    public Integer getPartnerTicketsLimit() {
        return partnerTicketsLimit;
    }

    public void setPartnerTicketsLimit(Integer partnerTicketsLimit) {
        this.partnerTicketsLimit = partnerTicketsLimit;
    }

    public Integer getPartnerCompanionTicketsLimit() {
        return partnerCompanionTicketsLimit;
    }

    public void setPartnerCompanionTicketsLimit(Integer partnerCompanionTicketsLimit) {
        this.partnerCompanionTicketsLimit = partnerCompanionTicketsLimit;
    }
}
