package es.onebox.mgmt.sessions.dto;

import es.onebox.mgmt.sessions.enums.SubscriptionListType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SessionSubscriptionListDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    private SubscriptionListType scope;
    private Integer id;

    public SubscriptionListType getScope() {
        return scope;
    }

    public void setScope(SubscriptionListType scope) {
        this.scope = scope;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

