package es.onebox.mgmt.datasources.ms.entity.dto.commElements;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EntityCommElementsImages extends java.util.ArrayList<EntityCommElementsImage> implements Serializable {

    @Serial
    private static final long serialVersionUID = 5178469914643403366L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
