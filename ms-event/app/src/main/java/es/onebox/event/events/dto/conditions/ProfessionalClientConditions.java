package es.onebox.event.events.dto.conditions;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public record ProfessionalClientConditions(
        ClientConditions conditions,
        Set<ClientActions> actions
) implements Serializable {
    @Serial
    private static final long serialVersionUID = -7248345402212177674L;
}
