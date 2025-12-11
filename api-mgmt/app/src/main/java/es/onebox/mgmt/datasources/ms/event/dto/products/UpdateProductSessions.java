package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.products.enums.SelectionType;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class UpdateProductSessions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Type can't be null")
    private SelectionType type;

    private Set<Long> sessions;

    public SelectionType getType() {
        return type;
    }

    public void setType(SelectionType type) {
        this.type = type;
    }

    public Set<Long> getSessions() {
        return sessions;
    }

    public void setSessions(Set<Long> sessions) {
        this.sessions = sessions;
    }
}
