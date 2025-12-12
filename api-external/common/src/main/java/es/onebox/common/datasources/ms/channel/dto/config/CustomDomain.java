package es.onebox.common.datasources.ms.channel.dto.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public record CustomDomain(
        String domain,
        @JsonProperty("default")
        Boolean defaultDomain
) implements Serializable {

    @Serial
    private static final long serialVersionUID = -8763892086528962261L;
}
