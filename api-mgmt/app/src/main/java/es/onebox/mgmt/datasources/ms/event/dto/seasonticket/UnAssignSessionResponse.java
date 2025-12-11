package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import es.onebox.mgmt.seasontickets.enums.SeasonTicketSessionUnAssignationReason;

import java.io.Serializable;

public class UnAssignSessionResponse implements Serializable {

    private static final long serialVersionUID = -3101648508934918369L;
    private Boolean result;
    private SeasonTicketSessionUnAssignationReason reason;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public SeasonTicketSessionUnAssignationReason getReason() {
        return reason;
    }

    public void setReason(SeasonTicketSessionUnAssignationReason reason) {
        this.reason = reason;
    }
}
