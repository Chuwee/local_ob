package es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCustomResourceAssetMsDTO(
        @JsonProperty("binary")
        String binary,

        @JsonProperty("filename")
        String filename
) {
}
