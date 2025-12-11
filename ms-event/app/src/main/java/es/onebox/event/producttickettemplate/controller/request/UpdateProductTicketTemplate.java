package es.onebox.event.producttickettemplate.controller.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateProductTicketTemplate(
		@Size(max = 50) @NotNull @Pattern(regexp = "^[a-zA-Z0-9 \\-()\\[\\]]*$", message = "Name can only contain letters, numbers, spaces, -, () and []") String name,
		@NotNull @Positive Integer modelId,
        @NotNull Integer defaultLanguageId,
		@NotEmpty List<Integer> selectedLanguageIds) {
}
