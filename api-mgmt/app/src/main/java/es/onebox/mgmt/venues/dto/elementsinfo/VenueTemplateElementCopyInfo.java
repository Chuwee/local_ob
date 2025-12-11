package es.onebox.mgmt.venues.dto.elementsinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.venues.enums.VenueTemplateElementCopyMatchType;

public record VenueTemplateElementCopyInfo(Long source,
                                           @JsonProperty("match_type") VenueTemplateElementCopyMatchType matchType) {

}