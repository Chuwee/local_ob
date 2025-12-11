package es.onebox.event.products.dao.couch;

import es.onebox.event.events.avetrestrictions.enums.AvetSectorRestrictionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AvetSectorRestriction implements Serializable {

    @Serial
    private static final long serialVersionUID = 2762731659213181211L;

    private String sid;
    private String name;
    private Map<String, String> translations;
    private AvetSectorRestrictionType type;
    private Long venueTemplateId;
    private Boolean activated;
    private List<Long> venueTemplateSectors;
    private Map<String, Object> fields;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public AvetSectorRestrictionType getType() {
        return type;
    }

    public void setType(AvetSectorRestrictionType type) {
        this.type = type;
    }

    public Long getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Long venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public List<Long> getVenueTemplateSectors() {
        return venueTemplateSectors;
    }

    public void setVenueTemplateSectors(List<Long> venueTemplateSectors) {
        this.venueTemplateSectors = venueTemplateSectors;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
