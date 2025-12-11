package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;

public class SeasonTicketLinkSeatResult implements Serializable {
    private static final long serialVersionUID = -6587800895668903453L;

    private Long id;

    private Boolean result;

    private SeasonTicketLinkSeatReason reason;

    public SeasonTicketLinkSeatResult() {
    }

    public SeasonTicketLinkSeatResult(Long id, Boolean result, SeasonTicketLinkSeatReason reason) {
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

    public SeasonTicketLinkSeatReason getReason() {
        return reason;
    }

    public void setReason(SeasonTicketLinkSeatReason reason) {
        this.reason = reason;
    }
}
