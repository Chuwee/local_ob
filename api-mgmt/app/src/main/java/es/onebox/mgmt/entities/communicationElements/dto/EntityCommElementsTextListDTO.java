package es.onebox.mgmt.entities.communicationElements.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class EntityCommElementsTextListDTO<T extends Serializable> extends ArrayList<EntityCommElementsTextDTO<T>> {

    @Serial
    private static final long serialVersionUID = 2029003634277342520L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}