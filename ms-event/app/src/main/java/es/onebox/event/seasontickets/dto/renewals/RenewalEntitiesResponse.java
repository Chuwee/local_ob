package es.onebox.event.seasontickets.dto.renewals;

import java.io.Serializable;
import java.util.List;

public class RenewalEntitiesResponse implements Serializable {
    private static final long serialVersionUID = -3926771211773832205L;

    private List<RenewalEntityDTO> renewalEntities;

    public RenewalEntitiesResponse() {
    }

    public List<RenewalEntityDTO> getRenewalEntities() {
        return renewalEntities;
    }

    public void setRenewalEntities(List<RenewalEntityDTO> renewalEntities) {
        this.renewalEntities = renewalEntities;
    }
}
