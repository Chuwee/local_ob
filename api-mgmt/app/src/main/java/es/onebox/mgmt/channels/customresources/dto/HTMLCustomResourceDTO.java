package es.onebox.mgmt.channels.customresources.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.customresources.enums.CustomResourceHTMLType;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;


public record HTMLCustomResourceDTO(
        @NotNull(message = "type must not be null")
        @JsonProperty("type")
        CustomResourceHTMLType type,

        @LanguageIETF
        @NotBlank(message = "language must not be empty")
        @JsonProperty("language")
        String language,

        @Length(max = 50000, message = "content max length is 50000")
        @JsonProperty("content")
        String content
) {

}
