package es.onebox.common.datasources.distribution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public record Presale(
        Long id,
        String name,
        @JsonProperty("session_id")
        Long idSession,
        @JsonProperty("collective_id")
        Long collectiveId
) implements Serializable {
    @Serial
    private static final long serialVersionUID = -8690471642477388886L;
}
