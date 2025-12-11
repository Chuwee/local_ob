package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;

public class DeleteRenewalsRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 1, max = 20)
    private List<String> renewalIds;

    public List<String> getRenewalIds() {
        return renewalIds;
    }

    public void setRenewalIds(List<String> renewalIds) {
        this.renewalIds = renewalIds;
    }
}
