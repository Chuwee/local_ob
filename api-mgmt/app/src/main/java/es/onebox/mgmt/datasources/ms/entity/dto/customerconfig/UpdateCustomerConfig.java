package es.onebox.mgmt.datasources.ms.entity.dto.customerconfig;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateCustomerConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -8553368740490077171L;

    private Integer entityId;
    private List<UpdateCustomerRestrictions> restrictions;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public List<UpdateCustomerRestrictions> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<UpdateCustomerRestrictions> restrictions) {
        this.restrictions = restrictions;
    }
}
