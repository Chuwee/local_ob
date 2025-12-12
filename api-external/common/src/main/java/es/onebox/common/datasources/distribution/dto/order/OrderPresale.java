package es.onebox.common.datasources.distribution.dto.order;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public record OrderPresale(
        Long id,
        String name,
        @JsonProperty("session_id")
        Long sessionId
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 5289474904812707351L;
}
