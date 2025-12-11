package es.onebox.mgmt.producttickettemplate.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateProductTicketTemplate(
		@Size(max = 50) @NotNull @Pattern(regexp = "^[a-zA-Z0-9 \\-()\\[\\]]*$", message = "Name can only contain letters, numbers, spaces, -, () and []") String name,
		@NotNull @Positive @JsonProperty("entity_id") Integer entityId,
		@NotNull @Positive @JsonProperty("model_id") Integer modelId) {
}
