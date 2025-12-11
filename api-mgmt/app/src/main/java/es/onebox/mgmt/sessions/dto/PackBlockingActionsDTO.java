package es.onebox.mgmt.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PackBlockingActionsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private PackBlockingAction action;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PackBlockingAction getAction() {
        return action;
    }

    public void setAction(PackBlockingAction action) {
        this.action = action;
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
