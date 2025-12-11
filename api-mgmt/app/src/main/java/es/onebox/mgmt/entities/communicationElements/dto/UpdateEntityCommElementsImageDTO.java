package es.onebox.mgmt.entities.communicationElements.dto;

import es.onebox.mgmt.entities.enums.EntityImageContentType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class UpdateEntityCommElementsImageDTO extends EntityCommElementsImageListDTO<EntityImageContentType> {

    @Serial
    private static final long serialVersionUID = 5786749996729264487L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
