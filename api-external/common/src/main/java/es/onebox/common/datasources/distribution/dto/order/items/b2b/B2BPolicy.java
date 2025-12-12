package es.onebox.common.datasources.distribution.dto.order.items.b2b;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public record B2BPolicy(
        @JsonProperty("policy_id")
        Long id
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 5216130858422216462L;
}
