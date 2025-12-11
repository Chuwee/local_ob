package es.onebox.mgmt.channels.customresources.assets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CreateCustomResourceAssetDTO(
        @JsonProperty("binary")
        @NotBlank(message = "binary must not be empty")
        // limit size to 4MB but add 35% extra from Base64 overhead relative to the original binary size
        @Size(max = ((int) (4 * 1024 * 1024 * 1.35)))
        String binary,

        @JsonProperty("filename")
        @NotBlank(message = "filename must not be empty")
        String filename
) {
}
