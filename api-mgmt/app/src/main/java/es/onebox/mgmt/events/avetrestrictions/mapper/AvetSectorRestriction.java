package es.onebox.mgmt.events.avetrestrictions.mapper;

import es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AvetSectorRestriction implements Serializable {
    @Serial
    private static final  long serialVersionUID = -8285367626953251451L;

    private String sid;
    private String name;
    private AvetSectorRestrictionType type;
    private Boolean activated;
    private Map<String, Object> fields;
    private Map<String, String> translations;
    private List<Long> venueTemplateSectors;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AvetSectorRestrictionType getType() {
        return type;
    }

    public void setType(AvetSectorRestrictionType type) {
        this.type = type;
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

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public List<Long> getVenueTemplateSectors() {
        return venueTemplateSectors;
    }

    public void setVenueTemplateSectors(List<Long> venueTemplateSectors) {
        this.venueTemplateSectors = venueTemplateSectors;
    }
}
