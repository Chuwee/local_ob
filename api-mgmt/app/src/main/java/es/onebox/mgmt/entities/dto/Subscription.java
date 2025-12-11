package es.onebox.mgmt.entities.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Subscription extends UpdateSubscriptionRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Long uses;
    private EntitySubscription entity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUses() {
        return uses;
    }

    public void setUses(Long uses) {
        this.uses = uses;
    }

    public EntitySubscription getEntity() {
        return entity;
    }

    public void setEntity(EntitySubscription entity) {
        this.entity = entity;
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
