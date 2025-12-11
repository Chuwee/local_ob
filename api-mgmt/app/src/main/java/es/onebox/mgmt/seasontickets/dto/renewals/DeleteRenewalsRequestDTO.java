package es.onebox.mgmt.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;

public class DeleteRenewalsRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 1, max = 20)
    @Valid
    @JsonProperty("renewal_ids")
    private List<String> renewalIds;

    public List<String> getRenewalIds() {
        return renewalIds;
    }

    public void setRenewalIds(List<String> renewalIds) {
        this.renewalIds = renewalIds;
    }
}
