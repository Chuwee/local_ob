package es.onebox.mgmt.datasources.ms.channel.dto.customresources;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public record CustomResourcesMsDTO(@JsonProperty("htmlResources")
                                   List<HTMLCustomResourceMsDTO> htmlResources,

                                   @JsonProperty("cssResources")
                                   List<CSSCustomResourceMsDTO> cssResources) implements Serializable {
}
