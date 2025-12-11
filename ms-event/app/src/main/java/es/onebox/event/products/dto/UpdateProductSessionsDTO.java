package es.onebox.event.products.dto;

import es.onebox.event.products.enums.SelectionType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class UpdateProductSessionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Set<Long> sessions;

    @NotNull(message = "Type can't be null")
    private SelectionType type;

    public Set<Long> getSessions() {
        return sessions;
    }

    public void setSessions(Set<Long> sessions) {
        this.sessions = sessions;
    }

    public SelectionType getType() {
        return type;
    }

    public void setType(SelectionType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
