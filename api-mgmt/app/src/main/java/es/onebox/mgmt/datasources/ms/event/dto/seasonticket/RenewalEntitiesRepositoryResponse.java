package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;
import java.util.List;

public class RenewalEntitiesRepositoryResponse implements Serializable {

    private static final long serialVersionUID = -2289313175282688769L;

    private List<RenewalEntity> renewalEntities;

    public RenewalEntitiesRepositoryResponse() {
    }

    public List<RenewalEntity> getRenewalEntities() {
        return renewalEntities;
    }

    public void setRenewalEntities(List<RenewalEntity> renewalEntities) {
        this.renewalEntities = renewalEntities;
    }
}
