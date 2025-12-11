package es.onebox.mgmt.seasontickets.dto.renewals;

import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateRenewalErrorReason;

import java.io.Serializable;

public class UpdateRenewalResponseItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Boolean result;
    private UpdateRenewalErrorReason reason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public UpdateRenewalErrorReason getReason() {
        return reason;
    }

    public void setReason(UpdateRenewalErrorReason reason) {
        this.reason = reason;
    }
}

