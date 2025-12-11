package es.onebox.mgmt.users.dto.realms;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record AvailableResourceCreateDTO(
        @NotNull
        List<String> resources
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1514688598153597539L;

}
