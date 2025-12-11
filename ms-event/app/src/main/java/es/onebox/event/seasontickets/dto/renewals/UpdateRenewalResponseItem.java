package es.onebox.event.seasontickets.dto.renewals;

import java.io.Serializable;

public class UpdateRenewalResponseItem implements Serializable {
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
