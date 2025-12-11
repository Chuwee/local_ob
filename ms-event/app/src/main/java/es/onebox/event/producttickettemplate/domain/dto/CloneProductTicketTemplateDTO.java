package es.onebox.event.producttickettemplate.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CloneProductTicketTemplateDTO(
        @Size(max = 50)
        @NotNull
        @Pattern(regexp = "^[a-zA-Z0-9 \\-()\\[\\]]*$", message = "Name can only contain letters, numbers, spaces, -, () and []") String name,
        @Positive Integer entityId) {
}
