package es.onebox.common.datasources.distribution.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public record OrderExpiration(
        @JsonProperty("value")
        Integer value,
        @JsonProperty("time_unit")
        String timeUnit,
        @JsonProperty("date")
        ZonedDateTime date
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 8516653620062698691L;
}
