package es.onebox.mgmt.channels.customresources.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CustomResourcesDTO(@JsonProperty("html_resources")
                                 List<HTMLCustomResourceDTO> htmlResources,

                                 @JsonProperty("css_resources")
                                 List<CSSCustomResourceDTO> cssResources) {
}
