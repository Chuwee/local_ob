package es.onebox.mgmt.channels.customresources.assets.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record CustomResourceAssetDTO(
        @JsonProperty("filename")
        String filename,

        @JsonProperty("url")
        String url
) implements Serializable {
}
