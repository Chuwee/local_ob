package es.onebox.mgmt.producttickettemplate.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.onebox.mgmt.events.dto.LanguagesDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record UpdateProductTicketTemplate(
        @Size(max = 50)
        @NotNull
        @Pattern(regexp = "^[a-zA-Z0-9 \\-()\\[\\]]*$", message = "Name can only contain letters, numbers, spaces, -, () and []")
        String name,

        @NotNull
        @Positive
        @JsonProperty("model_id")
        Integer modelId,

        @NotNull
        @JsonProperty("default")
        Boolean isDefault,

        @NotNull
        @Positive
        @JsonProperty("design_id")
        Long designId,

        LanguagesDTO languages) implements Serializable {
}
