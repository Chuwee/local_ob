package es.onebox.event.products.dto;

import es.onebox.event.products.enums.SelectionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class ProductSessionsResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Type can't be null")
    private SelectionType type;
    private ProductSessionsDTO sessions;


    public SelectionType getType() {
        return type;
    }

    public void setType(SelectionType type) {
        this.type = type;
    }

    public ProductSessionsDTO getSessions() {
        return sessions;
    }

    public void setSessions(ProductSessionsDTO sessions) {
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
