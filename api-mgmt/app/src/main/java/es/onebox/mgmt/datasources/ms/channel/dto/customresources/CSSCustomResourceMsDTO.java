package es.onebox.mgmt.datasources.ms.channel.dto.customresources;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.channel.enums.CustomResourceCSSType;

public record CSSCustomResourceMsDTO(
        @JsonProperty("type")
        CustomResourceCSSType type,

        @JsonProperty("content")
        String content) {
}
