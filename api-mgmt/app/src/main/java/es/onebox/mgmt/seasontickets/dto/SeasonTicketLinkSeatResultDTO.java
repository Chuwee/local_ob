package es.onebox.mgmt.seasontickets.dto;

import es.onebox.mgmt.seasontickets.enums.SeasonTicketLinkSeatReasonDTO;

import java.io.Serializable;

public class SeasonTicketLinkSeatResultDTO implements Serializable {
    private static final long serialVersionUID = -6587800895668903453L;

    private Long id;

    private Boolean result;

    private SeasonTicketLinkSeatReasonDTO reason;

    public SeasonTicketLinkSeatResultDTO() {
    }

    public SeasonTicketLinkSeatResultDTO(Long id, Boolean result, SeasonTicketLinkSeatReasonDTO reason) {
        this.id = id;
        this.result = result;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public SeasonTicketLinkSeatReasonDTO getReason() {
        return reason;
    }

    public void setReason(SeasonTicketLinkSeatReasonDTO reason) {
        this.reason = reason;
    }
}
