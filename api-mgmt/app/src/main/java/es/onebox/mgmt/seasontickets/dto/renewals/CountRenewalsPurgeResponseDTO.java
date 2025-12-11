package es.onebox.mgmt.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class CountRenewalsPurgeResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("deletable_renewals")
    private Integer deletableRenewals;

    public Integer getDeletableRenewals() {
        return deletableRenewals;
    }

    public void setDeletableRenewals(Integer deletableRenewals) {
        this.deletableRenewals = deletableRenewals;
    }
}
