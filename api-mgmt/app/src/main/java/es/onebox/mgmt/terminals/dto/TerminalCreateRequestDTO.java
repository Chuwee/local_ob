package es.onebox.mgmt.terminals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.TerminalType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import static java.lang.Integer.MAX_VALUE;

public record TerminalCreateRequestDTO(@NotBlank(message = "name is mandatory")
                                       @Size(max = 50, message = "name max size is 50")
                                       String name,
                                       @NotBlank(message = "code is mandatory")
                                       @Size(max = 65, message = "code max size is 65")
                                       String code,
                                       @JsonProperty("entity_id")
                                       @NotNull(message = "entity_id is mandatory")
                                       @Min(value = 1, message = "entity_id min value is 1")
                                       @Max(value = MAX_VALUE)
                                       Long entityId,
                                       @NotNull(message = "type is mandatory")
                                       TerminalType type,
                                       @JsonProperty("license_enabled")
                                       @NotNull(message = "license_enabled is mandatory")
                                       Boolean licenseEnabled) {

}