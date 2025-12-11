package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import es.onebox.mgmt.seasontickets.enums.SeasonTicketSessionAssignationReason;

import java.io.Serializable;

public class AssignSessionResponse implements Serializable {

    private static final long serialVersionUID = -3101648508934918369L;
    private Boolean result;
    private SeasonTicketSessionAssignationReason reason;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public SeasonTicketSessionAssignationReason getReason() {
        return reason;
    }

    public void setReason(SeasonTicketSessionAssignationReason reason) {
        this.reason = reason;
    }
}
