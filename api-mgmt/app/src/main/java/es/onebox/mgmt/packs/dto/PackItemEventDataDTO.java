package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PackItemEventDataDTO {

    @JsonProperty("venue_template")
    private PackItemVenueTemplateDTO venueTemplate;

    public PackItemVenueTemplateDTO getVenueTemplate() {
        return venueTemplate;
    }

    public void setVenueTemplate(PackItemVenueTemplateDTO venueTemplate) {
        this.venueTemplate = venueTemplate;
    }

}
