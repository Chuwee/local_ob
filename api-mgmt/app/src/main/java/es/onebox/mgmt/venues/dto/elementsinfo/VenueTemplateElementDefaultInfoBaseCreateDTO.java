package es.onebox.mgmt.venues.dto.elementsinfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VenueTemplateElementDefaultInfoBaseCreateDTO extends VenueTemplateElementInfoBaseRequestDTO {

    @JsonProperty("default_info")
    private VenueTemplateElementAggregatedInfoDTO defaultInfo;

    private Long source;

    public VenueTemplateElementAggregatedInfoDTO getDefaultInfo() {
        return defaultInfo;
    }

    public void setDefaultInfo(VenueTemplateElementAggregatedInfoDTO defaultInfo) {
        this.defaultInfo = defaultInfo;
    }

}
