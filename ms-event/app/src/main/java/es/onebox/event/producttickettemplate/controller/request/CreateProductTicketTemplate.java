package es.onebox.event.producttickettemplate.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateProductTicketTemplate(
		@Size(max = 50) @NotNull @Pattern(regexp = "^[a-zA-Z0-9 \\-()\\[\\]]*$", message = "Name can only contain letters, numbers, spaces, -, () and []") String name,
		@NotNull @Positive Integer entityId,
		@NotNull @Positive Integer modelId,
		@NotNull(message = "Default language id should come from entity") Integer defaultLanguageId) {
}
