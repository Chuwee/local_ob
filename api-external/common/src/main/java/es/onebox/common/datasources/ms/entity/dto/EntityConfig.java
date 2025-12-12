package es.onebox.common.datasources.ms.entity.dto;

import es.onebox.couchbase.annotations.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EntityConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 8825599003885868781L;

    @Id
    private Integer entityId;
    private Integer operatorId;
    private Boolean allowFeverZone;

    public EntityConfig() {
    }

    public EntityConfig(Integer id) {
        this.entityId = id;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Boolean getAllowFeverZone() {
        return allowFeverZone;
    }

    public void setAllowFeverZone(Boolean allowFeverZone) {
        this.allowFeverZone = allowFeverZone;
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
