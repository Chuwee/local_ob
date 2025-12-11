package es.onebox.event.events.avetrestrictions.dto;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class UpdateAvetSectorRestrictionDTO implements Serializable {
    @Serial
    private static final  long serialVersionUID = -8285367626953251451L;

    @Length(min = 1, max = 75, message = "restriction_name max size 75")
    private String name;
    private Boolean activated;
    private Map<String, Object> fields;
    private List<Long> venueTemplateSectors;
    private Map<String, String> translations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public List<Long> getVenueTemplateSectors() {
        return venueTemplateSectors;
    }

    public void setVenueTemplateSectors(List<Long> venueTemplateSectors) {
        this.venueTemplateSectors = venueTemplateSectors;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }
}
