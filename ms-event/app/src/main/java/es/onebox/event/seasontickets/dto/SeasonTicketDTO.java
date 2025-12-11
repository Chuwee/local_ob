package es.onebox.event.seasontickets.dto;

import es.onebox.event.events.enums.Provider;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketChangeSeat;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewal;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketDTO extends BaseSeasonTicketDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7944847398673688301L;

    private Integer sessionId;

    private SeasonTicketTicketTemplatesDTO seasonTicketTicketTemplatesDTO;

    private Boolean allowRenewal;
    private SeasonTicketRenewal renewal;

    private Boolean allowChangeSeat;
    private SeasonTicketChangeSeat changeSeat;

    private Boolean allowTransferTicket;
    private Boolean allowReleaseSeat;

    private Provider inventoryProvider;

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public SeasonTicketTicketTemplatesDTO getSeasonTicketTicketTemplatesDTO() {
        return seasonTicketTicketTemplatesDTO;
    }

    public void setSeasonTicketTicketTemplatesDTO(SeasonTicketTicketTemplatesDTO seasonTicketTicketTemplatesDTO) {
        this.seasonTicketTicketTemplatesDTO = seasonTicketTicketTemplatesDTO;
    }

    public Boolean getAllowRenewal() {
        return allowRenewal;
    }

    public void setAllowRenewal(Boolean allowRenewal) {
        this.allowRenewal = allowRenewal;
    }

    public SeasonTicketRenewal getRenewal() {
        return renewal;
    }

    public void setRenewal(SeasonTicketRenewal renewal) {
        this.renewal = renewal;
    }

    public Boolean getAllowChangeSeat() {
        return allowChangeSeat;
    }

    public void setAllowChangeSeat(Boolean allowChangeSeat) {
        this.allowChangeSeat = allowChangeSeat;
    }

    public SeasonTicketChangeSeat getChangeSeat() {
        return changeSeat;
    }

    public void setChangeSeat(SeasonTicketChangeSeat changeSeat) {
        this.changeSeat = changeSeat;
    }

    public Boolean getAllowTransferTicket() {
        return allowTransferTicket;
    }

    public void setAllowTransferTicket(Boolean allowTransferTicket) {
        this.allowTransferTicket = allowTransferTicket;
    }

    public Boolean getAllowReleaseSeat() {
        return allowReleaseSeat;
    }

    public void setAllowReleaseSeat(Boolean allowReleaseSeat) {
        this.allowReleaseSeat = allowReleaseSeat;
    }

    public Provider getInventoryProvider() {
        return inventoryProvider;
    }

    public void setInventoryProvider(Provider inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
