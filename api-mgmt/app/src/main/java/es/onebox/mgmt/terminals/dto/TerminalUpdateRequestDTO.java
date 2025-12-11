package es.onebox.mgmt.terminals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import static java.lang.Integer.MAX_VALUE;

public record TerminalUpdateRequestDTO(@Size(max = 50, message = "name max size is 50")
                                       String name,
                                       @JsonProperty("entity_id")
                                       @Min(value = 1, message = "entity_id min value is 1")
                                       @Max(MAX_VALUE)
                                       Long entityId,
                                       @JsonProperty("license_enabled")
                                       Boolean licenseEnabled) {

}
