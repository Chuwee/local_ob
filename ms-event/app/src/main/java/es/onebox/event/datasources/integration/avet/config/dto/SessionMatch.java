package es.onebox.event.datasources.integration.avet.config.dto;

import java.io.Serial;
import java.io.Serializable;

public class SessionMatch implements Serializable {

    @Serial
    private static final long serialVersionUID = -7268693228674214680L;

    private Long sessionId;
    private Integer avetMatchId;
    private String matchName;
    private Integer partnerTicketsLimit;
    private Integer partnerCompanionTicketsLimit;
    private Integer capacityId;
    private Boolean smartBookingContingency;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getAvetMatchId() {
        return avetMatchId;
    }

    public void setAvetMatchId(Integer avetMatchId) {
        this.avetMatchId = avetMatchId;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
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

    public Integer getCapacityId() {
        return capacityId;
    }

    public void setCapacityId(Integer capacityId) {
        this.capacityId = capacityId;
    }

    public Boolean getSmartBookingContingency() {
        return smartBookingContingency;
    }

    public void setSmartBookingContingency(Boolean smartBookingContingency) {
        this.smartBookingContingency = smartBookingContingency;
    }
}
