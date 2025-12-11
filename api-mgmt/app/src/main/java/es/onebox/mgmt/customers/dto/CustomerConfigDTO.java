package es.onebox.mgmt.customers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CustomerConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9112042679944324617L;

    @JsonProperty("entity_id")
    private Integer entityId;
    private List<CustomerRestrictionsDTO> restrictions;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public List<CustomerRestrictionsDTO> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<CustomerRestrictionsDTO> restrictions) {
        this.restrictions = restrictions;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
