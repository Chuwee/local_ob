package es.onebox.event.products.dto;

import es.onebox.event.products.enums.SelectionType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class ProductSessionsPublishingDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Type can't be null")
    private SelectionType type;
    private Set<ProductSessionBaseDTO> sessions;


    public SelectionType getType() {
        return type;
    }

    public void setType(SelectionType type) {
        this.type = type;
    }

    public Set<ProductSessionBaseDTO> getSessions() {
        return sessions;
    }

    public void setSessions(Set<ProductSessionBaseDTO> sessions) {
        this.sessions = sessions;
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
