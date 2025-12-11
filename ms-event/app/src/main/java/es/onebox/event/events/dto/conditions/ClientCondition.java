package es.onebox.event.events.dto.conditions;


import java.io.Serial;
import java.io.Serializable;

public record ClientCondition(
        Double value,
        String currency,
        ClientDiscountType type
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 5839868743764226341L;
}
