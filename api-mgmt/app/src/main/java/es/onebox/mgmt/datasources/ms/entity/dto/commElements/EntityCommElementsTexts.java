package es.onebox.mgmt.datasources.ms.entity.dto.commElements;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EntityCommElementsTexts extends java.util.ArrayList<EntityCommElementsText> implements Serializable {

    public EntityCommElementsTexts() {}

    @Serial
    private static final long serialVersionUID = 6698496025819361171L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
