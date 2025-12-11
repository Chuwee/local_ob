package es.onebox.mgmt.entities.communicationElements.dto;

import es.onebox.mgmt.common.CommunicationElementTextDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EntityCommElementsTextDTO<T extends Serializable> extends CommunicationElementTextDTO<T> {

    @Serial
    private static final long serialVersionUID = -529989706478521764L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
