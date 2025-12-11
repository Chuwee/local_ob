package es.onebox.mgmt.venues.dto.elementsinfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class VenueTemplateElementRestrictionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("enabled")
    private Boolean enabled;
    private Map<String, VenueTemplateElementRestrictionLanguageDTO> texts;


    public VenueTemplateElementRestrictionDTO() {
    }

    public VenueTemplateElementRestrictionDTO(Boolean enabled, Map<String, VenueTemplateElementRestrictionLanguageDTO> texts) {
        this.enabled = enabled;
        this.texts = texts;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, VenueTemplateElementRestrictionLanguageDTO> getTexts() {
        return texts;
    }

    public void setTexts(Map<String, VenueTemplateElementRestrictionLanguageDTO> texts) {
        this.texts = texts;
    }
}
