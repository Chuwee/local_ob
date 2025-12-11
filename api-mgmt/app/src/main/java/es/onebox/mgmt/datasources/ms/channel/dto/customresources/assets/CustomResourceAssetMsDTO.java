package es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record CustomResourceAssetMsDTO(
        @JsonProperty("filename")
        String filename,

        @JsonProperty("url")
        String url
) implements Serializable {
}
