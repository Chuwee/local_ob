package es.onebox.mgmt.datasources.ms.channel.dto.customresources;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.channel.enums.CustomResourceHTMLType;

public record HTMLCustomResourceMsDTO(
        @JsonProperty("type")
        CustomResourceHTMLType type,

        @JsonProperty("language")
        String language,

        @JsonProperty("content")
        String content) {
}
