package es.onebox.mgmt.seasontickets.dto.sessions;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SeasonTicketSessionAssignationFilter implements Serializable {
    @Serial
    private static final long serialVersionUID = 286743294586889904L;

    @JsonProperty("sessions")
    private List<Long> sessionList;

    @JsonProperty("update_barcodes")
    private Boolean updateBarcodes;

    public List<Long> getSessionList() {
        return sessionList;
    }

    public void setSessionList(List<Long> sessionList) {
        this.sessionList = sessionList;
    }

    public Boolean getUpdateBarcodes() {
        return updateBarcodes;
    }

    public void setUpdateBarcodes(Boolean updateBarcodes) {
        this.updateBarcodes = updateBarcodes;
    }
}
