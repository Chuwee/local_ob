package es.onebox.mgmt.datasources.ms.channel.dto.customresources;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UpdateCustomResourcesMsDTO(@JsonProperty("htmlResources")
                                         List<HTMLCustomResourceMsDTO> htmlResources,

                                         @JsonProperty("cssResources")
                                         List<CSSCustomResourceMsDTO> cssResources) {
}
