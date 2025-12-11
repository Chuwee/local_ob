package es.onebox.event.seasontickets.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;


public class SearchSeasonTicketDTO extends BaseSeasonTicketDTO implements Serializable {

    private static final long serialVersionUID = -2943620603726754633L;

    private SeasonTicketInternalGenerationStatus generationStatus;
    private Boolean allowRenewal;
    private Boolean allowChangeSeat;
    private Integer sessionId;

    public SeasonTicketInternalGenerationStatus getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(SeasonTicketInternalGenerationStatus generationStatus) {
        this.generationStatus = generationStatus;
    }

    public Boolean getAllowRenewal() {
        return allowRenewal;
    }

    public void setAllowRenewal(Boolean allowRenewal) {
        this.allowRenewal = allowRenewal;
    }

    public Boolean getAllowChangeSeat() {
        return allowChangeSeat;
    }

    public void setAllowChangeSeat(Boolean allowChangeSeat) {
        this.allowChangeSeat = allowChangeSeat;
    }

    public Integer getSessionId() {return sessionId;}

    public void setSessionId(Integer sessionId) {this.sessionId = sessionId;}

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
