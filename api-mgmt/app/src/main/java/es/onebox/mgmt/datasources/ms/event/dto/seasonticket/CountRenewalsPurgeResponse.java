package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serial;
import java.io.Serializable;

public class CountRenewalsPurgeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer deletableRenewals;

    public Integer getDeletableRenewals() {
        return deletableRenewals;
    }

    public void setDeletableRenewals(Integer deletableRenewals) {
        this.deletableRenewals = deletableRenewals;
    }
}
