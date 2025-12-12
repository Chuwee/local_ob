package es.onebox.common.datasources.distribution.dto.order.items;

import java.io.Serial;
import java.io.Serializable;

public record ItemAttendee(
        String key,
        String type,
        Boolean required,
        Object value
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 5687060420031722688L;
}
