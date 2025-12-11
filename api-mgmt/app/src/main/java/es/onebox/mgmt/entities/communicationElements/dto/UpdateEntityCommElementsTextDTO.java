package es.onebox.mgmt.entities.communicationElements.dto;

import es.onebox.mgmt.entities.enums.EntityTextContentType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class UpdateEntityCommElementsTextDTO extends EntityCommElementsTextListDTO<EntityTextContentType>{
    @Serial
    private static final long serialVersionUID = -5803336183998016257L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
