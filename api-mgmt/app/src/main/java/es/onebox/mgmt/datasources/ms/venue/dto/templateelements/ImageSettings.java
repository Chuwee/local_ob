package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

import java.util.List;
import java.util.Map;

public class ImageSettings {

    private Boolean enabled;
    private Map<String, List<ElementInfoImage>> images;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, List<ElementInfoImage>> getImages() {
        return images;
    }

    public void setImages(Map<String, List<ElementInfoImage>> images) {
        this.images = images;
    }

}

