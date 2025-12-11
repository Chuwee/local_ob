package es.onebox.mgmt.venues.dto.elementsinfo;

import java.util.List;
import java.util.Map;

public class VenueTemplateElementImageSettingsDTO {

    private Boolean enabled;
    private Map<String, List<VenueTemplateElementImageDTO>> images;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, List<VenueTemplateElementImageDTO>> getImages() {
        return images;
    }

    public void setImages(Map<String, List<VenueTemplateElementImageDTO>> images) {
        this.images = images;
    }

}
