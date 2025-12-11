package es.onebox.mgmt.entities.communicationElements.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EntityCommElementsImageListDTO<T extends Serializable> extends ArrayList<EntityCommElementsImageDTO<T>> {

    @Serial
    private static final long serialVersionUID = -3749369157809118181L;

    public EntityCommElementsImageListDTO() {
    }

    public EntityCommElementsImageListDTO(Collection<? extends EntityCommElementsImageDTO<T>> c) {
        super(c);
    }

    public List<EntityCommElementsImageDTO<T>> getImages() {
        return this;
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
