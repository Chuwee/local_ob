package es.onebox.mgmt.channels.customresources.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.customresources.enums.CustomResourceCSSType;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;


public record CSSCustomResourceDTO(
        @NotNull(message = "type must not be null")
        @JsonProperty("type")
        CustomResourceCSSType type,

        @Length(max = 50000, message = "content max length is 50000")
        @JsonProperty("content")
        String content
) {

}
