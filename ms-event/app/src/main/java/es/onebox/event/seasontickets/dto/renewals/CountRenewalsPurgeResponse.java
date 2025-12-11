package es.onebox.event.seasontickets.dto.renewals;

import java.io.Serial;
import java.io.Serializable;

public class CountRenewalsPurgeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer deletableRenewals;

    public CountRenewalsPurgeResponse(Integer deletableRenewals) {
        this.deletableRenewals = deletableRenewals;
    }

    public Integer getDeletableRenewals() {
        return deletableRenewals;
    }

    public void setDeletableRenewals(Integer deletableRenewals) {
        this.deletableRenewals = deletableRenewals;
    }
}
